plugins {
  kotlin("jvm") version embeddedKotlinVersion
  kotlin("plugin.serialization") version embeddedKotlinVersion
  application
  id("dev.jacomet.logging-capabilities") version "0.11.0"
  id("example-convention")
}

group = "org.jetbrains.experimental"

apply(from = "dummy.build.gradle.kts")

dependencies {
  implementation(kotlin("reflect"))
  implementation("org.gradle:gradle-tooling-api:8.6")

  implementation("org.slf4j:slf4j-simple:2.0.12")
  implementation("org.slf4j:slf4j-api:2.0.12")

  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

  testImplementation(kotlin("test"))
}

tasks.withType<Test>().configureEach {
  useJUnitPlatform()
}

application {
  mainClass = "org.jetbrains.experimental.gpde.GpdeKt"
}

loggingCapabilities {
  enforceSlf4JSimple()
}

kotlin {
  jvmToolchain(8)
}
