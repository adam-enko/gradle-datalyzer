package org.jetbrains.experimental.gpde.handlers

import org.gradle.tooling.GradleConnectionException
import org.gradle.tooling.ResultHandler
import org.gradle.tooling.model.GradleProject
import org.jetbrains.experimental.gpde.Reporter

internal class GradleProjectModelHandler(
  private val reporter: Reporter
) : ResultHandler<GradleProject> {
  override fun onComplete(result: GradleProject?) {
    if (result == null) {
      reporter.log("GradleProjectModelHandler got null GradleProject")
      return
    }

    (listOf(result) + result.children).forEach { gp ->
      try {
        reporter.collectScript(gp.name, gp.buildScript?.sourceFile)
        reporter.log("collected buildscript for ${gp.name}")
      } catch (t: Throwable) {
        reporter.log("failed to print buildscript for ${gp.name} : $t ${t.stackTraceToString()}")
      }
    }
  }

  override fun onFailure(failure: GradleConnectionException?) {
    reporter.log(
      "GradleProjectModelHandler failed to get GradleProject $failure\n" +
          failure?.stackTraceToString()?.prependIndent("  ")
    )
  }
}
