package org.jetbrains.experimental.gpde

import org.gradle.tooling.GradleConnector
import org.gradle.tooling.model.GradleProject
import org.gradle.tooling.model.HasGradleProject
import org.gradle.tooling.model.build.BuildEnvironment
import org.gradle.tooling.model.eclipse.EclipseProject
import org.gradle.tooling.model.gradle.BasicGradleProject
import org.gradle.tooling.model.gradle.GradleBuild
import org.gradle.tooling.model.idea.IdeaProject
import java.io.File
import kotlin.time.measureTime


fun main(args: Array<String>) {

  val projectDirArg = args.singleOrNull()
    ?: "."

  val outputFile = File("output/data.txt").apply {
    parentFile.mkdirs()
  }
  outputFile.writeText("")

  val collectedBuildScriptsDir = File("collected-scripts")
  collectedBuildScriptsDir.deleteRecursively()
  collectedBuildScriptsDir.mkdirs()

  val gradleConnector: GradleConnector = GradleConnector.newConnector()
    .forProjectDirectory(File(projectDirArg))

  gradleConnector.connect().use { project ->

    println("--- printing buildscripts... --- ")
    val gradleProject = project.getModel(GradleProject::class.java)

    (listOf(gradleProject) + gradleProject.children).forEach {
      try {
        println(
          "${it.name} buildscript   ---   " + it.buildScript?.sourceFile?.readText()?.lines()?.joinToString(" \\n ")
        )
      } catch (t: Throwable) {
        println("failed to print buildscript for ${it.name}")
      }
    }
    println("--- finished printing buildscripts --- ")

    project.action { build ->
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


    val buildEnvironment = project.getModel(BuildEnvironment::class.java)
    println("buildEnvironment.gradle.gradleVersion ${buildEnvironment.gradle.gradleVersion}")
    println("buildEnvironment.buildIdentifier ${buildEnvironment.buildIdentifier}")
    println("buildEnvironment.java.javaHome ${buildEnvironment.java.javaHome.invariantSeparatorsPath}")
    println("buildEnvironment.java.jvmArguments ${buildEnvironment.java.jvmArguments.joinToString()}")

    val eclipseProject = project.getModel(EclipseProject::class.java)
    println(
      "eclipseProject.gradleProject.buildScript" +
          eclipseProject.gradleProject.buildScript?.sourceFile?.readText()?.lines()?.joinToString(" \\n ")
    )

    val ideaProject = project.getModel(IdeaProject::class.java)

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
        project.newBuild().apply {
          addProgressListener(listener)
          forTasks(task)
          addArguments(args)
          setStandardOutput(System.out)
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
  }
}
