// SPDX-FileCopyrightText: © 2024 JetBrains s.r.o.
// SPDX-License-Identifier: Apache-2.0
package org.jetbrains.experimental.gpde.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.gradle.tooling.events.configuration.ProjectConfigurationFinishEvent
import org.gradle.tooling.events.configuration.ProjectConfigurationStartEvent


@Serializable
sealed interface ProjectConfigurationProgressEventData : ProgressEventData {
  override val descriptor: ProjectConfigurationOperationDescriptorData?
}

@Serializable
@SerialName("ProjectConfigurationFinish")
data class ProjectConfigurationFinishEventData(
  override val displayName: String,
  override val eventTime: Long,
  override val descriptor: ProjectConfigurationOperationDescriptorData? = null,
  val result: ProjectConfigurationOperationResultData? = null,
) : ProjectConfigurationProgressEventData {
  constructor(event: ProjectConfigurationFinishEvent) : this(
    displayName = event.displayName,
    eventTime = event.eventTime,
    descriptor = ProjectConfigurationOperationDescriptorData(event.descriptor),
    result = ProjectConfigurationOperationResultData(event.result),
  )
}

@Serializable
@SerialName("ProjectConfigurationStart")
data class ProjectConfigurationStartEventData(
  override val displayName: String,
  override val eventTime: Long,
  override val descriptor: ProjectConfigurationOperationDescriptorData? = null,
) : ProjectConfigurationProgressEventData {
  constructor(event: ProjectConfigurationStartEvent) : this(
    displayName = event.displayName,
    eventTime = event.eventTime,
    descriptor = ProjectConfigurationOperationDescriptorData(event.descriptor),
  )
}
