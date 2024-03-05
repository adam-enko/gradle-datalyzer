package org.jetbrains.experimental.gpde.utils

import com.github.ajalt.mordant.terminal.Terminal
import java.io.ByteArrayOutputStream
import java.io.OutputStream


internal class TeeOutputStream(
  private val outputs: List<OutputStream>
) : OutputStream() {

  constructor(
    vararg outputs: OutputStream
  ) : this(outputs.asList())

  override fun write(b: Int) {
    outputs.forEach { it.write(b) }
  }
}

internal class TerminalOutputStream(
  private val terminal: Terminal
) : OutputStream() {
  private val buffer = ByteArrayOutputStream()

  @Synchronized
  override fun close() {
    flush()
    buffer.close()
  }

  @Synchronized
  override fun flush() {
    terminal.muted(buffer.toByteArray().decodeToString())
    buffer.reset()
  }

  @Synchronized
  override fun write(b: Int) {
    buffer.write(b)
  }
}
