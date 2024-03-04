package org.jetbrains.experimental.gpde.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.gradle.tooling.events.test.*


@Serializable
sealed interface TestProgressEventData : ProgressEventData {
  override val descriptor: TestOperationDescriptorData?
}

@Serializable
@SerialName("TestFinish")
data class TestFinishEventData(
  override val displayName: String,
  override val eventTime: Long,
  override val descriptor: TestOperationDescriptorData? = null,
  val result: TestOperationResultData?
) : TestProgressEventData {
  constructor(event: TestFinishEvent) : this(
    displayName = event.displayName,
    eventTime = event.eventTime,
    descriptor = TestOperationDescriptorData(event.descriptor),
    result = TestOperationResultData(event.result)
  )
}

@Serializable
@SerialName("TestOutput")
data class TestOutputEventData(
  override val displayName: String,
  override val eventTime: Long,
  override val descriptor: TestOutputDescriptorData? = null,
) : TestProgressEventData {
  constructor(event: TestOutputEvent) : this(
    displayName = event.displayName,
    eventTime = event.eventTime,
    descriptor = TestOutputDescriptorData(event.descriptor),
  )
}

@Serializable
@SerialName("TestStart")
data class TestStartEventData(
  override val displayName: String,
  override val eventTime: Long,
  override val descriptor: TestOperationDescriptorData? = null,
) : TestProgressEventData {
  constructor(event: TestStartEvent) : this(
    displayName = event.displayName,
    eventTime = event.eventTime,
    descriptor = TestOperationDescriptorData(event.descriptor),
  )
}
