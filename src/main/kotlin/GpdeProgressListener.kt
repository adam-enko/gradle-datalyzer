package org.jetbrains.experimental.gpde

import com.github.ajalt.mordant.animation.Animation
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.gradle.tooling.events.ProgressEvent
import org.gradle.tooling.events.ProgressListener
import org.jetbrains.experimental.gpde.data.ProgressEventData
import java.io.BufferedWriter


internal class GpdeProgressListener(
  private val output: BufferedWriter,
  private val anim: Animation<String>,
) : ProgressListener {

  override fun statusChanged(event: ProgressEvent) {
    anim.update(event.displayName)
    val data = ProgressEventData(event)
    val dataEnc = Json.encodeToString(data)
    output.appendLine(dataEnc)
    output.flush()
  }
}
