// SPDX-FileCopyrightText: © 2024 JetBrains s.r.o.
// SPDX-License-Identifier: Apache-2.0
package datalyzer

plugins {
  base
}

tasks.withType<Test>().configureEach {
  useJUnitPlatform()
}

tasks.withType<AbstractArchiveTask>().configureEach {
  // https://docs.gradle.org/current/userguide/working_with_files.html#sec:reproducible_archives
  isPreserveFileTimestamps = false
  isReproducibleFileOrder = true
}


if (project == rootProject) {
  val projectVersion by tasks.registering {
    description = "Prints the project version"
    group = "help"
    val version = providers.provider { project.version.toString() }
    inputs.property("version", version)
    outputs.cacheIf("logging task, it should always run") { false }
    doLast {
      logger.quiet("${version.orNull}")
    }
  }
}
