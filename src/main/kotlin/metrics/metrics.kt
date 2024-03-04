package org.jetbrains.experimental.gpde.metrics


//import io.opentelemetry.exporter.logging.LoggingMetricExporter
//import io.opentelemetry.exporter.logging.LoggingSpanExporter
//import io.opentelemetry.exporter.logging.SystemOutLogRecordExporter
import io.opentelemetry.api.OpenTelemetry
import io.opentelemetry.api.baggage.propagation.W3CBaggagePropagator
import io.opentelemetry.api.trace.Tracer
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator
import io.opentelemetry.context.propagation.ContextPropagators
import io.opentelemetry.context.propagation.TextMapPropagator
import io.opentelemetry.exporter.otlp.logs.OtlpGrpcLogRecordExporter
import io.opentelemetry.exporter.otlp.metrics.OtlpGrpcMetricExporter
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter
import io.opentelemetry.sdk.OpenTelemetrySdk
import io.opentelemetry.sdk.logs.SdkLoggerProvider
import io.opentelemetry.sdk.logs.export.BatchLogRecordProcessor
import io.opentelemetry.sdk.metrics.SdkMeterProvider
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader
import io.opentelemetry.sdk.resources.Resource
import io.opentelemetry.sdk.trace.SdkTracerProvider
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor
import io.opentelemetry.semconv.ResourceAttributes


object Metrics {


//  val sdk: OpenTelemetrySdk = AutoConfiguredOpenTelemetrySdk.initialize()
//    .openTelemetrySdk

  fun openTelemetry(): OpenTelemetry {
    val resource = Resource.getDefault().toBuilder().put(ResourceAttributes.SERVICE_NAME, "gpde")
      .put(ResourceAttributes.SERVICE_VERSION, "0.1.0").build()

//    val sdkTracerProvider = SdkTracerProvider.builder()
//      .addSpanProcessor(SimpleSpanProcessor.create(LoggingSpanExporter.create()))
//      .setResource(resource)
//      .build()
//
//    val sdkMeterProvider = SdkMeterProvider.builder()
//      .registerMetricReader(PeriodicMetricReader.builder(LoggingMetricExporter.create()).build())
//      .setResource(resource)
//      .build()
//
//    val sdkLoggerProvider = SdkLoggerProvider.builder()
//      .setResource(resource)
//      .build()

    val jaegerEndpoint = "http://localhost:4317"


//    val jaegerOtlpExporter =
//      OtlpGrpcSpanExporter.builder()
//        .setEndpoint(jaegerEndpoint)
//        .setTimeout(30, TimeUnit.SECONDS)
//        .build()
//
//
//    val serviceNameResource =
//      Resource.create(Attributes.of(ResourceAttributes.SERVICE_NAME, "otel-jaeger-example"))
//
//
//    // Set to process the spans by the Jaeger Exporter
//    val tracerProvider =
//      SdkTracerProvider.builder()
//        .addSpanProcessor(BatchSpanProcessor.builder(jaegerOtlpExporter).build())
//        .setResource(Resource.getDefault().merge(serviceNameResource))
//        .build()


    val sdkTracerProvider2 = SdkTracerProvider.builder()
      .addSpanProcessor(
        BatchSpanProcessor.builder(OtlpGrpcSpanExporter.builder().setEndpoint(jaegerEndpoint).build()).build()
      )
      .setResource(resource)
      .build()

    val sdkMeterProvider2 = SdkMeterProvider.builder()
      .registerMetricReader(
        PeriodicMetricReader.builder(
          OtlpGrpcMetricExporter.builder().setEndpoint(jaegerEndpoint).build()
        ).build()
      )
      .setResource(resource)
      .build()

    val sdkLoggerProvider2 = SdkLoggerProvider.builder()
      .addLogRecordProcessor(
        BatchLogRecordProcessor.builder(OtlpGrpcLogRecordExporter.builder().setEndpoint(jaegerEndpoint).build()).build()
      )
      .setResource(resource)
      .build()

    val openTelemetry: OpenTelemetry = OpenTelemetrySdk.builder()
      .setTracerProvider(sdkTracerProvider2)
      .setMeterProvider(sdkMeterProvider2)
      .setLoggerProvider(sdkLoggerProvider2)
      .setPropagators(
        ContextPropagators.create(
          TextMapPropagator.composite(
            W3CTraceContextPropagator.getInstance(),
            W3CBaggagePropagator.getInstance()
          )
        )
      )
      .buildAndRegisterGlobal()

    // it's always a good idea to shut down the SDK cleanly at JVM exit.
    Runtime.getRuntime().addShutdownHook(Thread(sdkTracerProvider2::close))


    return openTelemetry
  }

  val openTelemetry = openTelemetry()

  val tracer: Tracer = openTelemetry.getTracer("instrumentation-scope-name", "instrumentation-scope-version")


  init {

//    val sdkTracerProvider = SdkTracerProvider.builder()
//      .addSpanProcessor(spanProcessor)
//      .setResource(resource)
//      .build()

//    val span: Span = tracer.spanBuilder("rollTheDice").startSpan()


//    val span2 = tracer.spanBuilder("asd")
//    span2.
  }
}
