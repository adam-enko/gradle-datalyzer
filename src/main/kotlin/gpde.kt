package org.jetbrains.experimental.gpde

import io.opentelemetry.api.trace.Span
import io.opentelemetry.context.Context
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.gradle.tooling.GradleConnector
import org.gradle.tooling.events.*
import org.gradle.tooling.model.GradleProject
import org.gradle.tooling.model.HasGradleProject
import org.gradle.tooling.model.build.BuildEnvironment
import org.gradle.tooling.model.eclipse.EclipseProject
import org.gradle.tooling.model.gradle.BasicGradleProject
import org.gradle.tooling.model.gradle.GradleBuild
import org.gradle.tooling.model.idea.IdeaProject
import org.jetbrains.experimental.gpde.data.FinishEventData
import org.jetbrains.experimental.gpde.data.ProgressEventData
import org.jetbrains.experimental.gpde.data.StartEventData
import org.jetbrains.experimental.gpde.metrics.Metrics
import org.jetbrains.experimental.gpde.utils.start
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.measureTime


fun main(args: Array<String>) {

  val projectDirArg = args.singleOrNull()
    ?: "."
//    ?: "/Users/dev/projects/jetbrains/kotlin/kotlin"
//    ?: error("No args received. Arg must be a Gradle project directory.")

  val outputFile = File("output/data.txt").apply {
    parentFile.mkdirs()
  }
  outputFile.writeText("")

  val collectedBuildScriptsDir = File("collected-scripts")
  collectedBuildScriptsDir.deleteRecursively()
  collectedBuildScriptsDir.mkdirs()

  Metrics.tracer.spanBuilder("gpde main").start { mainSpan ->

    //  val gradleHomeFolder: File = File("./build/gid-home").absoluteFile
//  gradleHomeFolder.mkdirs()
//
    fun gradleConnector(): GradleConnector = GradleConnector.newConnector()
      .forProjectDirectory(File(projectDirArg))

    gradleConnector().connect().use { project ->

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

        Metrics.tracer.spanBuilder("run task '$task'")
          .setParent(Context.current().with(mainSpan))
          .setAttribute("gradle.task.args", args.joinToString())
          .start { taskSpan ->

            val listener = GpdeProgressListener(outputFile, taskSpan)

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
      }

      run("help")
      run("assemble")
      run("tasks")
//      run(":kotlin-stdlib:dependencies")
//      run(":kotlin.kotlin-gradle-plugin-api:dependencies")
//      run("clean")
//      run("assemble", args = listOf("--no-build-cache"))
    }


//ProcessBuilder().apply {
//    this.directory(File("."))
//        .command("")
//}

  }
}


//class BuildScriptCollector(
//  output: File
//) : ProgressListener {
//  private val output = output.printWriter()
//
//  override fun statusChanged(event: ProgressEvent) {
//    TODO("Not yet implemented")
//  }
//}

class GpdeProgressListener(
  output: File,
  private val taskSpan: Span,
) : ProgressListener {

  private val output = output.printWriter()

  private val spans = ConcurrentHashMap<OperationDescriptor, Span>()

  override fun statusChanged(event: ProgressEvent) {
    val data = ProgressEventData(event)

//    val eventDescName = event.descriptor.fullName()

//
//    val span = spans.getOrPut(eventDescName) {
////    val parentName = event.descriptor.parent?.name
////    val parentSpan = spans[parentName]
//      Metrics.tracer.spanBuilder(eventDescName).apply {
////        if (parentSpan != null) {
////          setParent(parentSpan)
////        }
//      }
//        .startSpan()
//    }

    val parent: OperationDescriptor? = event.descriptor.parent
    if (
//      true
//      data is StartEvent || data is FinishEvent
      data !is StartEventData && data !is FinishEventData // ignore generic events, because there's too many of them
    ) {
      if (parent != null) {
        if (event is StartEvent) {
          //        val parent = event.descriptor.parent?.fullName()?.let { spans[it] }
          var parentParent: OperationDescriptor? = parent.parent
          var parentSpan: Span? = null
          while (parentParent != null) {
            parentSpan = spans[parentParent]
            if (parentSpan != null) break
            parentParent = parentParent.parent
          }

          //        val parent = event.descriptor.parent?.let { spans[it] }

          //        val span = Metrics.tracer.spanBuilder(eventDescName).apply {
          val span = Metrics.tracer.spanBuilder(parent.displayName)
            .setParent(Context.current().with(parentSpan ?: taskSpan))
            .startSpan()


          spans[parent] = span
          span.setAttribute("gradle.progressEvent.type", event::class.java.name)
          span.setAttribute("gradle.progressEvent.displayName", event.displayName)
          span.setAttribute("gradle.progressEvent.eventTime", event.eventTime)
        } else if (event is FinishEvent) {
          spans[parent]?.end()
        }
      }
    }

//    val span: Span = tracer.spanBuilder(eventDescName).startSpan()


    val dataEnc = Json.encodeToString(data)
//    println("event: $dataEnc")
    output.println(dataEnc)
    output.flush()
  }
}


tailrec fun OperationDescriptor.fullName(name: String = this.name): String {
  val parent = parent ?: return name
  return parent.fullName("${parent.name} > $name")
}
