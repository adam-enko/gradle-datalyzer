package org.jetbrains.experimental.gpde

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.gradle.tooling.events.ProgressEvent
import org.gradle.tooling.events.ProgressListener
import org.jetbrains.experimental.gpde.data.ProgressEventData
import java.io.BufferedWriter


internal class GpdeProgressListener(
  private val output: BufferedWriter,
) : ProgressListener {

  override fun statusChanged(event: ProgressEvent) {
    val data = ProgressEventData(event)

    val dataEnc = Json.encodeToString(data)
    output.appendLine(dataEnc)
    output.flush()
  }
}
