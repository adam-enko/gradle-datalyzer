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
version = "0.0.0-SNAPSHOT"

dependencies {
  implementation(kotlin("reflect"))
  implementation("org.gradle:gradle-tooling-api:8.6")

  implementation("org.slf4j:slf4j-simple:2.0.12")
  implementation("org.slf4j:slf4j-api:2.0.12")

  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

  implementation("com.github.ajalt.clikt:clikt:4.2.2")

  testImplementation(kotlin("test"))
}

application {
  applicationName = "datalyzer"
  mainClass = "org.jetbrains.experimental.gradle.datalyzer.MainKt"
}

loggingCapabilities {
  enforceSlf4JSimple()
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
        |$optionsBlockStartTag
        |${datalyzerHelp.get()}
      """.trimMargin(),
    )

    readme.writeText(updatedReadme)
  }
}

tasks.assemble {
  dependsOn(updateReadMeUsage)
}
