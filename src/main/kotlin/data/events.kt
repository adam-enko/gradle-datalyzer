// SPDX-FileCopyrightText: © 2024 JetBrains s.r.o.
// SPDX-License-Identifier: Apache-2.0
package org.jetbrains.experimental.gradle.datalyzer.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.gradle.tooling.events.FinishEvent
import org.gradle.tooling.events.ProgressEvent
import org.gradle.tooling.events.StartEvent
import org.gradle.tooling.events.StatusEvent
import org.gradle.tooling.events.configuration.ProjectConfigurationFinishEvent
import org.gradle.tooling.events.configuration.ProjectConfigurationStartEvent
import org.gradle.tooling.events.download.FileDownloadFinishEvent
import org.gradle.tooling.events.download.FileDownloadStartEvent
import org.gradle.tooling.events.lifecycle.BuildPhaseFinishEvent
import org.gradle.tooling.events.lifecycle.BuildPhaseStartEvent
import org.gradle.tooling.events.problems.ProblemEvent
import org.gradle.tooling.events.task.TaskFinishEvent
import org.gradle.tooling.events.task.TaskStartEvent
import org.gradle.tooling.events.test.TestFinishEvent
import org.gradle.tooling.events.test.TestOutputEvent
import org.gradle.tooling.events.test.TestStartEvent
import org.gradle.tooling.events.transform.TransformFinishEvent
import org.gradle.tooling.events.transform.TransformStartEvent
import org.gradle.tooling.events.work.WorkItemFinishEvent
import org.gradle.tooling.events.work.WorkItemStartEvent


@Suppress("UnstableApiUsage")
fun ProgressEventData(event: ProgressEvent): ProgressEventData {
  return when (event) {
    is BuildPhaseStartEvent            -> BuildPhaseStartEventData(event)
    is BuildPhaseFinishEvent           -> BuildPhaseFinishEventData(event)

    is FileDownloadStartEvent          -> FileDownloadStartEventData(event)
    is FileDownloadFinishEvent         -> FileDownloadFinishEventData(event)

    is ProblemEvent                    -> ProblemEventData(event)

    is ProjectConfigurationStartEvent  -> ProjectConfigurationStartEventData(event)
    is ProjectConfigurationFinishEvent -> ProjectConfigurationFinishEventData(event)
//    is ProjectConfigurationProgressEvent -> ProjectConfigurationProgressEventData(event)

    is StatusEvent                     -> StatusEventData(event)
    is TaskFinishEvent                 -> TaskFinishEventData(event)
    is TaskStartEvent                  -> TaskStartEventData(event)
    is TestFinishEvent                 -> TestFinishEventData(event)
    is TestOutputEvent                 -> TestOutputEventData(event)
    is TestStartEvent                  -> TestStartEventData(event)
//    is TestProgressEvent                 -> TestProgressEventData(event)

    is TransformFinishEvent            -> TransformFinishEventData(event)
    is TransformStartEvent             -> TransformStartEventData(event)

    is WorkItemStartEvent              -> WorkItemStartEventData(event)
    is WorkItemFinishEvent             -> WorkItemFinishEventData(event)

//    is TaskProgressEvent                 -> TaskProgressEventData(event)
    is StartEvent                      -> StartEventData(event)
    is FinishEvent                     -> FinishEventData(event)
    else                               -> UnknownEventData(event)
  }
}


@Serializable
sealed interface ProgressEventData {
  val displayName: String
  val eventTime: Long
  val descriptor: OperationDescriptorData?
}

//@Serializable
//sealed interface StatusEventData {
//  val displayName: String
//  val eventTime: Long
//  val descriptor: OperationDescriptorData?
//}


@Serializable
@SerialName("Unknown")
data class UnknownEventData(
  override val displayName: String,
  override val eventTime: Long,
  override val descriptor: OperationDescriptorData? = null,
) : ProgressEventData {
  constructor(event: ProgressEvent) : this(
    displayName = event.displayName,
    eventTime = event.eventTime,
    descriptor = OperationDescriptorData(event.descriptor),
  )
}


@Serializable
@SerialName("Finish")
data class FinishEventData(
  override val displayName: String,
  override val eventTime: Long,
  override val descriptor: OperationDescriptorData? = null,
) : ProgressEventData {
  constructor(event: FinishEvent) : this(
    displayName = event.displayName,
    eventTime = event.eventTime,
    descriptor = OperationDescriptorData(event.descriptor),
  )
}

@Serializable
@SerialName("Problem")
data class ProblemEventData(
  override val displayName: String,
  override val eventTime: Long,
  override val descriptor: BaseProblemDescriptorData? = null,
) : ProgressEventData {
  constructor(event: ProblemEvent) : this(
    displayName = event.displayName,
    eventTime = event.eventTime,
    descriptor = BaseProblemDescriptorData(event.descriptor),
  )
}


@Serializable
@SerialName("Start")
data class StartEventData(
  override val displayName: String,
  override val eventTime: Long,
  override val descriptor: OperationDescriptorData? = null,
) : ProgressEventData {
  constructor(event: StartEvent) : this(
    displayName = event.displayName,
    eventTime = event.eventTime,
    descriptor = OperationDescriptorData(event.descriptor),
  )
}

@Serializable
@SerialName("Status")
data class StatusEventData(
  override val displayName: String,
  override val eventTime: Long,
  override val descriptor: OperationDescriptorData? = null,
  val progress: Long,
  val total: Long,
  val unit: String,
) : ProgressEventData {
  constructor(event: StatusEvent) : this(
    displayName = event.displayName,
    eventTime = event.eventTime,
    descriptor = OperationDescriptorData(event.descriptor),
    progress = event.progress,
    total = event.total,
    unit = event.unit,
  )
}
