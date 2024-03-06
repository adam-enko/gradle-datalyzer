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
