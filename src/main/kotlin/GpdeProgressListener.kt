package org.jetbrains.experimental.gpde

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.gradle.tooling.events.ProgressEvent
import org.gradle.tooling.events.ProgressListener
import org.jetbrains.experimental.gpde.data.ProgressEventData
import java.io.File


internal class GpdeProgressListener(
  output: File,
) : ProgressListener {

  private val output = output.printWriter()

  override fun statusChanged(event: ProgressEvent) {
    val data = ProgressEventData(event)

    val dataEnc = Json.encodeToString(data)
    output.println(dataEnc)
    output.flush()
  }
}
