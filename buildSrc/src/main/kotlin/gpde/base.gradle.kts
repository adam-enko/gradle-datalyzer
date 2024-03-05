package gpde

import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.withType

plugins {
  base
}

tasks.withType<Test>().configureEach {
  useJUnitPlatform()
}
