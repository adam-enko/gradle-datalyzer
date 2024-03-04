package org.jetbrains.experimental.gpde.utils

import io.opentelemetry.api.trace.Span
import io.opentelemetry.api.trace.SpanBuilder

internal fun SpanBuilder.start(block: (span: Span) -> Unit) {
  var span: Span? = null
  try {
    span = startSpan()
    block(span)
  } catch (ex: Throwable) {
    ex.printStackTrace()
    span?.recordException(ex)
  } finally {
    span?.end()
  }
}
