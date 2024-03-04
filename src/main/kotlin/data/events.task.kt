package org.jetbrains.experimental.gpde.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.gradle.tooling.events.task.TaskFinishEvent
import org.gradle.tooling.events.task.TaskStartEvent


@Serializable
@SerialName("TaskFinish")
data class TaskFinishEventData(
  override val displayName: String,
  override val eventTime: Long,
  override val descriptor: TaskOperationDescriptorData? = null,
  val result: TaskOperationResultData? = null,
) : ProgressEventData {
  constructor(event: TaskFinishEvent) : this(
    displayName = event.displayName,
    eventTime = event.eventTime,
    descriptor = TaskOperationDescriptorData(event.descriptor),
    result = TaskOperationResultData(event.result)
  )
}

//@Serializable
//@SerialName("TaskProgress")
//data class TaskProgressEventData(
//  override val displayName: String,
//  override val eventTime: Long,
//  override val descriptor: TaskOperationDescriptorData? = null,
//) : ProgressEventData {
//  constructor(event: TaskProgressEvent) : this(
//    displayName = event.displayName,
//    eventTime = event.eventTime,
//    descriptor = TaskOperationDescriptorData(event.descriptor),
//  )
//}

@Serializable
@SerialName("TaskStart")
data class TaskStartEventData(
  override val displayName: String,
  override val eventTime: Long,
  override val descriptor: TaskOperationDescriptorData? = null,
) : ProgressEventData {
  constructor(event: TaskStartEvent) : this(
    displayName = event.displayName,
    eventTime = event.eventTime,
    descriptor = TaskOperationDescriptorData(event.descriptor),
  )
}
