
### gradle-datalyzer

/Users/dev/projects/jetbrains/gradle/gradle-datalyzer/build.gradle.kts

```
// SPDX-FileCopyrightText: © 2024 JetBrains s.r.o.
// SPDX-License-Identifier: Apache-2.0
import org.gradle.api.tasks.PathSensitivity.NONE
import org.gradle.kotlin.dsl.support.serviceOf

plugins {
  id("datalyzer.base")
  kotlin("jvm") version embeddedKotlinVersion
  kotlin("plugin.serialization") version embeddedKotlinVersion
  application
  id("dev.jacomet.logging-capabilities") version "0.11.0"
}

group = "org.jetbrains.experimental.gradle.datalyzer"
project.version = object {
  private val gitVersion = project.gitVersion
  override fun toString(): String = gitVersion.get()
}

dependencies {
  implementation("org.gradle:gradle-tooling-api:8.6")

  implementation("org.slf4j:slf4j-simple:2.0.12")
  implementation("org.slf4j:slf4j-api:2.0.12")

  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

  implementation("com.github.ajalt.clikt:clikt:4.2.2")

  testImplementation(kotlin("test"))
}

loggingCapabilities {
  enforceSlf4JSimple()
}

application {
  applicationName = "datalyzer"
  mainClass = "org.jetbrains.experimental.gradle.datalyzer.MainKt"
}

kotlin {
  jvmToolchain(8)
  compilerOptions {
    optIn.addAll("kotlin.io.path.ExperimentalPathApi")
  }
}

tasks.distZip {
  archiveVersion.set("")
}

tasks.distTar {
  archiveVersion.set("")
}

val updateReadMeUsage by tasks.registering {
  group = project.name

  val readme = file("README.md")
  outputs.file(readme).withPropertyName("readme")

  dependsOn(tasks.installDist)
  val datalyzer = tasks.installDist.map { it.destinationDir.resolve("bin/datalyzer") }
  inputs.file(datalyzer).withPropertyName("datalyzer").withPathSensitivity(NONE)

  onlyIf { "win" !in System.getProperty("os.name").lowercase() }

  val providers = serviceOf<ProviderFactory>()

  val optionsBlockStartTag = "```shell datalyzer-options"

  doLast {
    @Suppress("UnstableApiUsage")
    val datalyzerHelp = providers.exec {
      executable(datalyzer.get())
      args("--help")
    }.standardOutput.asText

    val readmeText = readme.readText()
    val startIndex = readmeText.indexOf(optionsBlockStartTag)
      .takeIf { it > 0 } ?: error("README is missing datalyzer-options block")
    val endIndex = readmeText.indexOf("```", startIndex = startIndex + optionsBlockStartTag.length)
      .takeIf { it > 0 } ?: error("could not find end of datalyzer-options block")

    val updatedReadme = readmeText.replaceRange(
      startIndex = startIndex,
      endIndex = endIndex,
      replacement = """
$optionsBlockStartTag
${datalyzerHelp.get()}
      """.trimMargin(),
    )

    readme.writeText(updatedReadme)
  }
}

tasks.assemble {
  dependsOn(updateReadMeUsage)
}

```


### gradle-datalyzer settings

/Users/dev/projects/jetbrains/gradle/gradle-datalyzer/settings.gradle.kts

```
// SPDX-FileCopyrightText: © 2024 JetBrains s.r.o.
// SPDX-License-Identifier: Apache-2.0
@file:Suppress("UnstableApiUsage")

rootProject.name = "gradle-datalyzer"

pluginManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
  }
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)

  repositories {
    mavenCentral()
    maven("https://repo.gradle.org/gradle/libs-releases") {
      name = "GradleLibs"
    }
  }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

//region git versioning
val gitDescribe: Provider<String> =
  providers
    .exec {
      workingDir(rootDir)
      commandLine(
        "git",
        "describe",
        "--always",
        "--tags",
        "--dirty=-DIRTY",
        "--broken=-BROKEN",
        "--match=v[0-9]*\\.[0-9]*\\.[0-9]*",
      )
      isIgnoreExitValue = true
    }.standardOutput.asText.map { it.trim() }

val currentBranchName: Provider<String> =
  providers
    .exec {
      workingDir(rootDir)
      commandLine(
        "git",
        "branch",
        "--show-current",
      )
      isIgnoreExitValue = true
    }.standardOutput.asText.map { it.trim() }

val currentCommitHash: Provider<String> =
  providers.exec {
    workingDir(rootDir)
    commandLine(
      "git",
      "rev-parse",
      "--short",
      "HEAD",
    )
    isIgnoreExitValue = true
  }.standardOutput.asText.map { it.trim() }

/**
 * The standard Gradle way of setting the version, which can be set on the CLI with
 *
 * ```shell
 * ./gradlew -Pversion=1.2.3
 * ```
 *
 * This can be used to override [gitVersion].
 */
val standardVersion: Provider<String> = providers.gradleProperty("version")

/** Match simple SemVer tags. The first group is the `major.minor.patch` digits. */
val semverRegex = Regex("""v((?:0|[1-9][0-9]*)\.(?:0|[1-9][0-9]*)\.(?:0|[1-9][0-9]*))""")

val gitVersion: Provider<String> =
  gitDescribe.zip(currentBranchName) { described, branch ->
    val detached = branch.isNullOrBlank()

    if (!detached) {
      "$branch-SNAPSHOT"
        // control chars and slashes aren't allowed in Maven Versions
        .map { c -> if (c.isISOControl() || c == '/' || c == '\\') "_" else c }
        .joinToString("")
    } else {
      val descriptions = described.split("-")
      val head = descriptions.singleOrNull() ?: ""
      // drop the leading `v`, try to find the `major.minor.patch` digits group
      val headVersion = semverRegex.matchEntire(head)?.groupValues?.last()
      headVersion
        ?: currentCommitHash.orNull // fall back to using the git commit hash
        ?: "unknown" // just in case there's no git repo, e.g. someone downloaded a zip archive
    }
  }

gradle.allprojects {
  extensions.add<Provider<String>>("gitVersion", standardVersion.orElse(gitVersion))
}
//endregion

```

