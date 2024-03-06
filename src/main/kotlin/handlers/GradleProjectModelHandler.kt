// SPDX-FileCopyrightText: © 2024 JetBrains s.r.o.
// SPDX-License-Identifier: Apache-2.0
package org.jetbrains.experimental.gpde.handlers

import org.gradle.tooling.GradleConnectionException
import org.gradle.tooling.ResultHandler
import org.gradle.tooling.model.GradleProject
import org.jetbrains.experimental.gpde.Reporter

internal class GradleProjectModelHandler(
  private val reporter: Reporter,
) : ResultHandler<GradleProject> {
  override fun onComplete(result: GradleProject?) {
    if (result == null) {
      reporter.logWarning("GradleProjectModelHandler got null GradleProject")
      return
    }

    reporter.logInfo("finding all projects for $result")
    val allProjects = ArrayDeque<GradleProject>()
    val queue = ArrayDeque<GradleProject>()
    queue += result
    while (queue.isNotEmpty()) {
      val p = queue.removeLast()
      reporter.log("  found project $p")
      if (allProjects.none { it.path == p.path }) {
        allProjects += p
        queue += p.children
      }
    }


    allProjects.forEach { gp ->
      try {
        reporter.collectScript(gp.name, gp.buildScript?.sourceFile)
        reporter.log("collected buildscript for ${gp.name}")

        if (gp.projectDirectory.exists() && gp.projectDirectory.resolve("settings.gradle.kts").exists()) {
          reporter.collectScript("${gp.name} settings", gp.projectDirectory.resolve("settings.gradle.kts"))
        }
        if (gp.projectDirectory.exists() && gp.projectDirectory.resolve("settings.gradle").exists()) {
          reporter.collectScript("${gp.name} settings", gp.projectDirectory.resolve("settings.gradle"))
        }

      } catch (t: Throwable) {
        reporter.log("failed to print buildscript for ${gp.name} : $t ${t.stackTraceToString()}")
      }
    }
  }

  override fun onFailure(failure: GradleConnectionException?) {
    reporter.logWarning(
      "GradleProjectModelHandler failed to get GradleProject $failure\n" +
          failure?.stackTraceToString()?.prependIndent("  ")
    )
  }
}
