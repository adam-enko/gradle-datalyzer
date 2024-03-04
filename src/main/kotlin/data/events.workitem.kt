package org.jetbrains.experimental.gpde.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.gradle.tooling.events.work.WorkItemFinishEvent
import org.gradle.tooling.events.work.WorkItemStartEvent


sealed interface WorkItemProgressEventData : ProgressEventData {
  override val descriptor: WorkItemOperationDescriptorData?
}


@Serializable
@SerialName("WorkItemFinish")
data class WorkItemFinishEventData(
  override val displayName: String,
  override val eventTime: Long,
  override val descriptor: WorkItemOperationDescriptorData? = null,
) : WorkItemProgressEventData {
  constructor(event: WorkItemFinishEvent) : this(
    displayName = event.displayName,
    eventTime = event.eventTime,
    descriptor = WorkItemOperationDescriptorData(event.descriptor),
  )
}


@Serializable
@SerialName("WorkItemStart")
data class WorkItemStartEventData(
  override val displayName: String,
  override val eventTime: Long,
  override val descriptor: WorkItemOperationDescriptorData? = null,
) : WorkItemProgressEventData {
  constructor(event: WorkItemStartEvent) : this(
    displayName = event.displayName,
    eventTime = event.eventTime,
    descriptor = WorkItemOperationDescriptorData(event.descriptor),
  )
}
