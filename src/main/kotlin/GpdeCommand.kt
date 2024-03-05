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
import org.gradle.tooling.model.HasGradleProject
import org.gradle.tooling.model.build.BuildEnvironment
import org.gradle.tooling.model.eclipse.EclipseProject
import org.gradle.tooling.model.gradle.BasicGradleProject
import org.gradle.tooling.model.gradle.GradleBuild
import org.gradle.tooling.model.idea.IdeaProject
import org.jetbrains.experimental.gpde.handlers.GradleProjectModelHandler
import org.jetbrains.experimental.gpde.utils.TerminalOutputStream
import java.io.File
import java.nio.file.Path
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME
import kotlin.io.path.*
import kotlin.time.measureTime


internal class GpdeCommand : CliktCommand() {

  private val gradleProjectDir: Path by option(help = "Location of the Gradle Project")
    .path()
    .default(Path("."))
    .check("Must be a directory, but was a file") { it.isDirectory() }

  private val outputDir: Path by option(help = "Output report directory")
    .path()
    .defaultLazy {
      val prettyDate = ZonedDateTime.now().format(ISO_ZONED_DATE_TIME)
        .map { if (it.isLetterOrDigit()) it else "-" }.joinToString("")
      Path("./reports/${prettyDate}/")
    }
    .check("Must be an empty directory") {
      @OptIn(ExperimentalPathApi::class)
      !it.exists() || it.isDirectory() && it.walk().drop(1).count() == 0
    }

  override fun run() {

    val reporter = Reporter(outputDir)

    val outputFile = File("output/data.txt").apply {
      parentFile.mkdirs()
    }
    outputFile.writeText("")

    val gradleConnector: GradleConnector = GradleConnector.newConnector()
      .forProjectDirectory(gradleProjectDir.toFile())

    gradleConnector.connect().use { connection ->

      println("--- printing buildscripts... --- ")

      val gpmh = GradleProjectModelHandler(reporter)
      connection.getModel(GradleProject::class.java, gpmh)


      connection.action { build ->
        val allProjects = ArrayDeque<BasicGradleProject>()

        build.buildModel.let { bm -> if (bm is HasGradleProject) bm.gradleProject else null }
          ?.let { gp ->
            println("!!! project ${gp.buildScript?.sourceFile?.readText()?.lines()?.joinToString(" \\n ")} ")
          }

        build.buildModel.projects.forEach { p ->
          allProjects.add(p)
          allProjects.addAll(p.children)

          if (p is HasGradleProject) {
            println("project ${p.gradleProject?.buildScript?.sourceFile?.readText()?.lines()?.joinToString(" \\n ")} ")
          }
        }


        val allGradleBuilds = ArrayDeque<GradleBuild>()

        build.buildModel.editableBuilds.forEach { gb ->

          val settings1 = gb.rootProject.projectDirectory.resolve("settings.gradle.kts")
          val settings2 = gb.rootProject.projectDirectory.resolve("settings.gradle")

          gb.rootProject
          gb.projects
          gb.editableBuilds
          gb.includedBuilds
        }
        build.buildModel.includedBuilds.forEach { gb ->
          gb.projects
          gb.editableBuilds
          gb.includedBuilds
        }
      }


      val buildEnvironment = connection.getModel(BuildEnvironment::class.java)
      println("buildEnvironment.gradle.gradleVersion ${buildEnvironment.gradle.gradleVersion}")
      println("buildEnvironment.buildIdentifier ${buildEnvironment.buildIdentifier}")
      println("buildEnvironment.java.javaHome ${buildEnvironment.java.javaHome.invariantSeparatorsPath}")
      println("buildEnvironment.java.jvmArguments ${buildEnvironment.java.jvmArguments.joinToString()}")

      val eclipseProject = connection.getModel(EclipseProject::class.java)
      println(
        "eclipseProject.gradleProject.buildScript" +
            eclipseProject.gradleProject.buildScript?.sourceFile?.readText()?.lines()?.joinToString(" \\n ")
      )

      val ideaProject = connection.getModel(IdeaProject::class.java)

      ideaProject.modules.forEach { im ->
//        im.dependencies.joinToString { it.scope.scope }
        im.contentRoots.forEach { content ->
          content.sourceDirectories.forEach { src ->
            println("${ideaProject.name} - src${if (src.isGenerated) "generated" else ""}: ${src.directory.invariantSeparatorsPath}")
          }
        }
      }

      fun run(task: String, args: List<String> = emptyList()) {
        val listener = GpdeProgressListener(outputFile)

        val time = measureTime {
          connection.newBuild().apply {
            addProgressListener(listener)
            forTasks(task)
            addArguments(args)
            setStandardOutput(TerminalOutputStream(terminal))
            run()
          }
        }
        println(" ~ ran '$task' in $time")
      }

//      run("clean")
      run("help")
      run("assemble")
      run("tasks")
      run("check")
//      run(":kotlin-stdlib:dependencies")
//      run(":kotlin.kotlin-gradle-plugin-api:dependencies")
//      run("clean")
//      run("assemble", args = listOf("--no-build-cache"))

      reporter.zip()
    }
  }
}
