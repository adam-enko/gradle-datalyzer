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
      } catch (t: Throwable) {
        reporter.log("failed to print buildscript for ${gp.name} : $t")
      }
    }
    reporter.log("--- finished printing buildscripts --- ")
  }

  override fun onFailure(failure: GradleConnectionException?) {

  }
}
