// SPDX-FileCopyrightText: © 2024 JetBrains s.r.o.
// SPDX-License-Identifier: Apache-2.0
package org.jetbrains.experimental.gpde.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.gradle.tooling.events.download.FileDownloadFinishEvent
import org.gradle.tooling.events.download.FileDownloadStartEvent

@Serializable
sealed interface FileDownloadProgressEventData : ProgressEventData {
  override val descriptor: FileDownloadOperationDescriptorData?
}

@Serializable
@SerialName("FileDownloadFinish")
data class FileDownloadFinishEventData(
  override val displayName: String,
  override val eventTime: Long,
  override val descriptor: FileDownloadOperationDescriptorData? = null,
) : FileDownloadProgressEventData {
  constructor(event: FileDownloadFinishEvent) : this(
    displayName = event.displayName,
    eventTime = event.eventTime,
    descriptor = FileDownloadOperationDescriptorData(event.descriptor),
  )
}

@Serializable
@SerialName("FileDownloadStart")
data class FileDownloadStartEventData(
  override val displayName: String,
  override val eventTime: Long,
  override val descriptor: FileDownloadOperationDescriptorData? = null,
) : FileDownloadProgressEventData {
  constructor(event: FileDownloadStartEvent) : this(
    displayName = event.displayName,
    eventTime = event.eventTime,
    descriptor = FileDownloadOperationDescriptorData(event.descriptor),
  )
}
