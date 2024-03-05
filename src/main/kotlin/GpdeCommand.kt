package org.jetbrains.experimental.gpde

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.parameters.options.check
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.defaultLazy
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.path
import org.gradle.tooling.GradleConnector
import org.gradle.tooling.model.GradleProject
import org.gradle.tooling.model.build.BuildEnvironment
import org.gradle.tooling.model.idea.IdeaProject
import org.jetbrains.experimental.gpde.handlers.GradleProjectModelHandler
import org.jetbrains.experimental.gpde.utils.replaceNonAlphaNumeric
import java.io.File
import java.nio.file.Path
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.walk
import kotlin.time.measureTime


internal class GpdeCommand : CliktCommand() {

  private val gradleProjectDir: Path by option(help = "Location of the Gradle Project")
    .path()
    .default(Path("."))
    .check("Must be a directory, but was a file") { it.isDirectory() }

  private val outputDir: Path by option(help = "Output report directory")
    .path()
    .defaultLazy {
      val datetime = ZonedDateTime.now().format(ISO_ZONED_DATE_TIME).replaceNonAlphaNumeric()
      Path("./reports/${datetime}/")
    }
    .check("Must be an empty directory") {
      !it.exists() || it.isDirectory() && it.walk().drop(1).count() == 0
    }

  override fun run() {

    val reporter = Reporter(outputDir, terminal)

    val outputFile = File("output/data.txt").apply {
      parentFile.mkdirs()
    }
    outputFile.writeText("")

    val gradleConnector: GradleConnector = GradleConnector.newConnector()
      .forProjectDirectory(gradleProjectDir.toFile())

    gradleConnector.connect().use { connection ->

      val gpmh = GradleProjectModelHandler(reporter)
      connection.getModel(GradleProject::class.java, gpmh)

      connection.action { build ->
//        val allProjects = ArrayDeque<BasicGradleProject>()
//
//        build.buildModel.projects.forEach { p ->
//          allProjects.add(p)
//          allProjects.addAll(p.children)
//        }
//
//        val allGradleBuilds = ArrayDeque<GradleBuild>()


        build.buildModel.editableBuilds.forEach { gb ->
          listOf(
            gb.rootProject.projectDirectory.resolve("settings.gradle.kts"),
            gb.rootProject.projectDirectory.resolve("settings.gradle"),
          ).forEach { settings ->
            if (settings.exists() && settings.isFile) {
              reporter.collectScript("${gb.rootProject.name} settings.gradle", settings)
            }
          }

//          gb.rootProject
//          gb.projects
//          gb.editableBuilds
//          gb.includedBuilds
        }
//        build.buildModel.includedBuilds.forEach { gb ->
//          gb.projects
//          gb.editableBuilds
//          gb.includedBuilds
//        }
      }


      val buildEnvironment = connection.getModel(BuildEnvironment::class.java)
      reporter.log("buildEnvironment.gradle.gradleVersion ${buildEnvironment.gradle.gradleVersion}")
      reporter.log("buildEnvironment.buildIdentifier ${buildEnvironment.buildIdentifier}")
      reporter.log("buildEnvironment.java.javaHome ${buildEnvironment.java.javaHome.invariantSeparatorsPath}")
      reporter.log("buildEnvironment.java.jvmArguments ${buildEnvironment.java.jvmArguments.joinToString()}")

      val ideaProject = connection.getModel(IdeaProject::class.java)

      ideaProject.modules.forEach { im ->
//        im.dependencies.joinToString { it.scope.scope }
        im.contentRoots.forEach { content ->
          content.sourceDirectories.forEach { src ->
            reporter.log("sourceDirectory - ${ideaProject.name} - src${if (src.isGenerated) "generated" else ""}: ${src.directory.invariantSeparatorsPath}")
          }
        }
      }

      fun run(task: String, args: List<String> = emptyList()) {
        reporter.log("Running task $task, args:$args")

        val taskStdout = reporter.taskOutput(task)
        val taskData = reporter.taskData(task)

        try {
          val listener = GpdeProgressListener(taskData)

          val time = measureTime {
            connection.newBuild().apply {
              addProgressListener(listener)
              forTasks(task)
              addArguments(args)
              setStandardOutput(taskStdout)
              run()
            }
          }
          reporter.log(" ~ ran $task in $time")
        } catch (t: Throwable) {
          reporter.log("task $task failed $t ${t.stackTraceToString()}")
        } finally {
          taskStdout.flush()
          taskStdout.close()
          taskData.flush()
          taskData.close()
        }
      }

//      run("clean")
      run("help", listOf("--no-configuration-cache", "--no-build-cache", "--rerun-tasks"))
      run("tasks")
      run("assemble")
//      run("check")
//      run(":kotlin-stdlib:dependencies")
//      run(":kotlin.kotlin-gradle-plugin-api:dependencies")
//      run("clean")
//      run("assemble", args = listOf("--no-build-cache"))

      reporter.zip()
    }
  }
}
