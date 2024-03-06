// SPDX-FileCopyrightText: © 2024 JetBrains s.r.o.
// SPDX-License-Identifier: Apache-2.0
package org.jetbrains.experimental.gradle.datalyzer.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.gradle.tooling.events.lifecycle.BuildPhaseFinishEvent
import org.gradle.tooling.events.lifecycle.BuildPhaseStartEvent

@Serializable
sealed interface BuildPhaseProgressEventData : ProgressEventData {
  override val descriptor: BuildPhaseOperationDescriptorData?
}

@Serializable
@SerialName("BuildPhaseFinish")
data class BuildPhaseFinishEventData(
  override val displayName: String,
  override val eventTime: Long,
  override val descriptor: BuildPhaseOperationDescriptorData? = null,
  val result: OperationResultData? = null,
) : BuildPhaseProgressEventData {
  constructor(event: BuildPhaseFinishEvent) : this(
    displayName = event.displayName,
    eventTime = event.eventTime,
    descriptor = BuildPhaseOperationDescriptorData(event.descriptor),
    result = OperationResultData(event.result),
  )
}

@Serializable
@SerialName("BuildPhaseStart")
data class BuildPhaseStartEventData(
  override val displayName: String,
  override val eventTime: Long,
  override val descriptor: BuildPhaseOperationDescriptorData? = null,
) : BuildPhaseProgressEventData {
  constructor(event: BuildPhaseStartEvent) : this(
    displayName = event.displayName,
    eventTime = event.eventTime,
    descriptor = BuildPhaseOperationDescriptorData(event.descriptor),
  )
}
