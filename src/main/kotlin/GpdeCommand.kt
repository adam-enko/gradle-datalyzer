// SPDX-FileCopyrightText: © 2024 JetBrains s.r.o.
// SPDX-License-Identifier: Apache-2.0
package org.jetbrains.experimental.gpde

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.parameters.options.check
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.defaultLazy
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.path
import com.github.ajalt.mordant.animation.textAnimation
import org.gradle.tooling.GradleConnector
import org.gradle.tooling.model.GradleProject
import org.gradle.tooling.model.build.BuildEnvironment
import org.gradle.tooling.model.idea.IdeaProject
import org.jetbrains.experimental.gpde.handlers.GradleProjectModelHandler
import org.jetbrains.experimental.gpde.utils.replaceNonAlphaNumeric
import java.nio.file.Path
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME
import kotlin.io.path.*
import kotlin.time.measureTime


internal class GpdeCommand : CliktCommand() {

  private val gradleProjectDir: Path by option("--projectDir", help = "Location of the Gradle Project")
    .path()
    .default(Path(".").absolute())
    .check("Must be a directory, but was a file") { it.isDirectory() }

  private val reportsDir: Path by option("--reportsDir", help = "Output reports directory")
    .path()
    .defaultLazy {
      val datetime = OffsetDateTime.now().format(ISO_OFFSET_DATE_TIME)
      val reportDir = "${gradleProjectDir.absolute().name}-${datetime}".replaceNonAlphaNumeric()
      Path("./gpde-reports/$reportDir/")
    }
    .check("Must be an empty directory") {
      !it.exists() || it.isDirectory() && it.walk().drop(1).count() == 0
    }

  private val reporter: Reporter by lazy { Reporter(reportsDir, terminal) }

  override fun run() {
    terminal.info("starting gradle-project-data-extractor...")
    terminal.info("   Analysing project $gradleProjectDir")
    terminal.info("   Report will be generated into $reportsDir")

    collectBuildScriptData()
    collectEnvironmentData()
    collectTaskData()
  }

  private fun gradleConnector(): GradleConnector = GradleConnector.newConnector()
    .forProjectDirectory(gradleProjectDir.toFile())

  private fun collectBuildScriptData() {

    gradleConnector().connect().use { connection ->

      val gpmh = GradleProjectModelHandler(reporter)
      connection.getModel(GradleProject::class.java, gpmh)

//      connection.action { build ->
////        val allProjects = ArrayDeque<BasicGradleProject>()
////
////        build.buildModel.projects.forEach { p ->
////          allProjects.add(p)
////          allProjects.addAll(p.children)
////        }
////
////        val allGradleBuilds = ArrayDeque<GradleBuild>()
//
//
//        build.buildModel.editableBuilds.flatMap { gb ->
//          listOf(
//            gb.rootProject.projectDirectory.resolve("settings.gradle.kts"),
//            gb.rootProject.projectDirectory.resolve("settings.gradle"),
//          )
//
////          gb.rootProject
////          gb.projects
////          gb.editableBuilds
////          gb.includedBuilds
//        }
////        build.buildModel.includedBuilds.forEach { gb ->
////          gb.projects
////          gb.editableBuilds
////          gb.includedBuilds
////        }
//      }.run()
//        .forEach { settings ->
//          if (settings.exists() && settings.isFile) {
//            reporter.collectScript("settings.gradle", settings)
//          }
//        }
    }
  }

  private fun collectEnvironmentData() {
    gradleConnector().connect().use { connection ->
      val buildEnvironment = connection.getModel(BuildEnvironment::class.java)
      reporter.log("buildEnvironment.gradle.gradleVersion ${buildEnvironment.gradle.gradleVersion}")
      reporter.log("buildEnvironment.buildIdentifier ${buildEnvironment.buildIdentifier}")
      reporter.log("buildEnvironment.java.javaHome ${buildEnvironment.java.javaHome.invariantSeparatorsPath}")
      reporter.log("buildEnvironment.java.jvmArguments ${buildEnvironment.java.jvmArguments.joinToString()}")

      val ideaProject = connection.getModel(IdeaProject::class.java)

      ideaProject.modules.forEach { im ->
        im.contentRoots.forEach { content ->
          content.sourceDirectories.forEach { src ->
            reporter.log("sourceDirectory - ${ideaProject.name} - src ${if (src.isGenerated) "generated" else ""}: ${src.directory.invariantSeparatorsPath}")
          }
        }
      }
    }
  }

  private fun collectTaskData() {

    gradleConnector().connect().use { connection ->

      fun run(task: String, args: List<String> = emptyList()) {
        reporter.logInfo("Running task $task, args:$args")

        val taskStatusAnim = terminal.textAnimation<String> { txt ->
          synchronized(terminal) {
            txt.take(terminal.info.width - 5)
          }
        }

        val taskStdout = reporter.taskOutput(task)
        val taskData = reporter.taskData(task)

        val time = measureTime {
          try {
            val listener = GpdeProgressListener(taskData, taskStatusAnim)

            connection.newBuild().apply {
              addProgressListener(listener)
              forTasks(task)
              addArguments(args)
              setStandardOutput(taskStdout)
              setStandardError(taskStdout)
              run()
            }
          } catch (t: Throwable) {
            reporter.log(
              " caught task $task failure $t\n${t.stackTraceToString().prependIndent("  ")}"
            )
          } finally {
            taskStdout.flush()
            taskStdout.close()
            taskData.flush()
            taskData.close()
            taskStatusAnim.stop()
          }
        }
        reporter.logInfo(" ~ finished running $task in $time")
      }

//      run("clean")
      run("help", listOf("--no-configuration-cache", "--no-build-cache", "--rerun-tasks"))
      run("tasks")
      run("assemble", listOf("--dry-run"))
      run("check", listOf("--dry-run"))
      run("build", listOf("--dry-run"))
      run("assemble")
//      run("check")
//      run(":kotlin-stdlib:dependencies")
//      run(":kotlin.kotlin-gradle-plugin-api:dependencies")
//      run("clean")
//      run("assemble", args = listOf("--no-build-cache"))

      val reportZip = reporter.zip()
      terminal.info("finished gradle-project-data-extractor")
      terminal.success("Created zip report $reportZip")
    }
  }
}
