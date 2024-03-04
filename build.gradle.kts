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

  // using the bom ensures that all of your opentelemetry dependency versions are aligned
  implementation(platform("io.opentelemetry:opentelemetry-bom:1.35.0"))
  implementation(platform("io.opentelemetry.instrumentation:opentelemetry-instrumentation-bom:2.1.0"))

  implementation("io.opentelemetry:opentelemetry-api")
  implementation("io.opentelemetry:opentelemetry-sdk")
  implementation("io.opentelemetry:opentelemetry-exporter-otlp")


//  implementation(platform("io.opentelemetry:opentelemetry-bom:1.35.0"))
//  implementation("io.opentelemetry:opentelemetry-api")
//  implementation("io.opentelemetry:opentelemetry-sdk")
//  implementation("io.opentelemetry:opentelemetry-exporter-logging")
  implementation("io.opentelemetry.semconv:opentelemetry-semconv:1.23.1-alpha")
  implementation("io.opentelemetry:opentelemetry-sdk-extension-autoconfigure")
//  implementation("io.opentelemetry:opentelemetry-exporter-otlp")
////  implementation("io.opentelemetry:opentelemetry-exporter-sender-okhttp")
//
//  runtimeOnly("io.opentelemetry.instrumentation:opentelemetry-log4j-appender-2.17:1.35.0")
//
//
//
//  runtimeOnly("io.grpc:grpc-netty-shaded:1.61.0")
//  implementation("io.grpc:grpc-protobuf:1.61.0")
//  implementation("io.grpc:grpc-stub:1.61.0")
//  compileOnly("org.apache.tomcat:annotations-api:6.0.53")  // necessary for Java 9+


//  implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.12.4")
//  implementation("org.apache.logging.log4j:log4j-api:2.23.0")
//  implementation("org.apache.logging.log4j:log4j-core:2.23.0")

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
