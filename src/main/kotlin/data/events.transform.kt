package org.jetbrains.experimental.gpde.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.gradle.tooling.events.transform.TransformFinishEvent
import org.gradle.tooling.events.transform.TransformStartEvent


@Serializable
sealed interface TransformProgressEventData : ProgressEventData {
  override val descriptor: TransformOperationDescriptorData?
}


@Serializable
@SerialName("TransformFinish")
data class TransformFinishEventData(
  override val displayName: String,
  override val eventTime: Long,
  override val descriptor: TransformOperationDescriptorData? = null,
) : TransformProgressEventData {
  constructor(event: TransformFinishEvent) : this(
    displayName = event.displayName,
    eventTime = event.eventTime,
    descriptor = TransformOperationDescriptorData(event.descriptor),
  )
}


@Serializable
@SerialName("TransformStart")
data class TransformStartEventData(
  override val displayName: String,
  override val eventTime: Long,
  override val descriptor: TransformOperationDescriptorData? = null,
) : TransformProgressEventData {
  constructor(event: TransformStartEvent) : this(
    displayName = event.displayName,
    eventTime = event.eventTime,
    descriptor = TransformOperationDescriptorData(event.descriptor),
  )
}