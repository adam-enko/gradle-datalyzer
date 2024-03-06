// SPDX-FileCopyrightText: © 2024 JetBrains s.r.o.
// SPDX-License-Identifier: Apache-2.0
package org.jetbrains.experimental.gpde

import com.github.ajalt.mordant.animation.Animation
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.gradle.tooling.events.ProgressEvent
import org.gradle.tooling.events.ProgressListener
import org.jetbrains.experimental.gpde.data.ProgressEventData
import org.jetbrains.experimental.gpde.utils.truncate
import java.io.BufferedWriter
import java.util.concurrent.atomic.AtomicLong
import kotlin.time.DurationUnit.SECONDS
import kotlin.time.TimeSource

internal class GpdeProgressListener(
  private val output: BufferedWriter,
  private val taskStatusAnim: Animation<String>,
) : ProgressListener {

  private val count = AtomicLong()
  private val start = TimeSource.Monotonic.markNow()

  override fun statusChanged(event: ProgressEvent) {
    taskStatusAnim.update("[${start.elapsedNow().truncate(SECONDS)} | ${count.incrementAndGet()}] ${event.displayName}")
    val data = ProgressEventData(event)
    val dataEnc = Json.encodeToString(data)
    output.appendLine(dataEnc)
    output.flush()
  }
}
