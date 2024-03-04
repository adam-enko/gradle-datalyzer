package org.jetbrains.experimental.gpde.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.gradle.tooling.events.FinishEvent
import org.gradle.tooling.events.ProgressEvent
import org.gradle.tooling.events.StartEvent
import org.gradle.tooling.events.StatusEvent
import org.gradle.tooling.events.configuration.ProjectConfigurationFinishEvent
import org.gradle.tooling.events.configuration.ProjectConfigurationProgressEvent
import org.gradle.tooling.events.configuration.ProjectConfigurationStartEvent
import org.gradle.tooling.events.download.FileDownloadFinishEvent
import org.gradle.tooling.events.download.FileDownloadProgressEvent
import org.gradle.tooling.events.download.FileDownloadStartEvent
import org.gradle.tooling.events.lifecycle.BuildPhaseFinishEvent
import org.gradle.tooling.events.lifecycle.BuildPhaseProgressEvent
import org.gradle.tooling.events.lifecycle.BuildPhaseStartEvent
import org.gradle.tooling.events.problems.ProblemEvent
import org.gradle.tooling.events.task.TaskFinishEvent
import org.gradle.tooling.events.task.TaskOperationDescriptor
import org.gradle.tooling.events.task.TaskProgressEvent
import org.gradle.tooling.events.task.TaskStartEvent
import org.gradle.tooling.events.test.TestFinishEvent
import org.gradle.tooling.events.test.TestOutputEvent
import org.gradle.tooling.events.test.TestProgressEvent
import org.gradle.tooling.events.test.TestStartEvent
import org.gradle.tooling.events.transform.TransformFinishEvent
import org.gradle.tooling.events.transform.TransformProgressEvent
import org.gradle.tooling.events.transform.TransformStartEvent
import org.gradle.tooling.events.work.WorkItemFinishEvent
import org.gradle.tooling.events.work.WorkItemProgressEvent
import org.gradle.tooling.events.work.WorkItemStartEvent


@Suppress("UnstableApiUsage")
fun ProgressEventData(event: ProgressEvent): ProgressEventData? {
  return when (event) {
    is FileDownloadStartEvent            -> FileDownloadStartEventData(event)
    is BuildPhaseStartEvent              -> BuildPhaseStartEventData(event)
    is BuildPhaseFinishEvent             -> BuildPhaseFinishEventData(event)
    is BuildPhaseProgressEvent           -> BuildPhaseProgressEventData(event)
    is FileDownloadFinishEvent           -> FileDownloadFinishEventData(event)
    is FileDownloadProgressEvent         -> FileDownloadProgressEventData(event)
    is ProblemEvent                      -> ProblemEventData(event)

    is ProjectConfigurationStartEvent    -> ProjectConfigurationStartEventData(event)
    is ProjectConfigurationFinishEvent   -> ProjectConfigurationFinishEventData(event)
//    is ProjectConfigurationProgressEvent -> ProjectConfigurationProgressEventData(event)

    is StatusEvent                       -> StatusEventData(event)
    is TaskFinishEvent                   -> TaskFinishEventData(event)
    is TaskStartEvent                    -> TaskStartEventData(event)
    is TestFinishEvent                   -> TestFinishEventData(event)
    is TestOutputEvent                   -> TestOutputEventData(event)
    is TestStartEvent                    -> TestStartEventData(event)
//    is TestProgressEvent                 -> TestProgressEventData(event)
    is TransformFinishEvent              -> TransformFinishEventData(event)
    is TransformStartEvent               -> TransformStartEventData(event)
    is TransformProgressEvent            -> TransformProgressEventData(event)
    is WorkItemStartEvent                -> WorkItemStartEventData(event)
    is WorkItemFinishEvent               -> WorkItemFinishEventData(event)
    is WorkItemProgressEvent             -> WorkItemProgressEventData(event)

//    is TaskProgressEvent                 -> TaskProgressEventData(event)
    is StartEvent                        -> StartEventData(event)
    is FinishEvent                       -> FinishEventData(event)
    else                                 -> null
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
@SerialName("BuildPhaseFinish")
data class BuildPhaseFinishEventData(
  override val displayName: String,
  override val eventTime: Long,
  override val descriptor: BuildPhaseOperationDescriptorData? = null,
  val result: OperationResultData?,
) : ProgressEventData {
  constructor(event: BuildPhaseFinishEvent) : this(
    displayName = event.displayName,
    eventTime = event.eventTime,
    descriptor = BuildPhaseOperationDescriptorData(event.descriptor),
    result = OperationResultData(event.result),
  )
}

@Serializable
@SerialName("BuildPhaseProgress")
data class BuildPhaseProgressEventData(
  override val displayName: String,
  override val eventTime: Long,
  override val descriptor: BuildPhaseOperationDescriptorData? = null,
) : ProgressEventData {
  constructor(event: BuildPhaseProgressEvent) : this(
    displayName = event.displayName,
    eventTime = event.eventTime,
    descriptor = BuildPhaseOperationDescriptorData(event.descriptor),
  )
}

@Serializable
@SerialName("BuildPhaseStart")
data class BuildPhaseStartEventData(
  override val displayName: String,
  override val eventTime: Long,
  override val descriptor: BuildPhaseOperationDescriptorData? = null,
) : ProgressEventData {
  constructor(event: BuildPhaseStartEvent) : this(
    displayName = event.displayName,
    eventTime = event.eventTime,
    descriptor = BuildPhaseOperationDescriptorData(event.descriptor),
  )
}

@Serializable
@SerialName("FileDownloadFinish")
data class FileDownloadFinishEventData(
  override val displayName: String,
  override val eventTime: Long,
  override val descriptor: FileDownloadOperationDescriptorData? = null,
) : ProgressEventData {
  constructor(event: FileDownloadFinishEvent) : this(
    displayName = event.displayName,
    eventTime = event.eventTime,
    descriptor = FileDownloadOperationDescriptorData(event.descriptor),
  )
}

@Serializable
@SerialName("FileDownloadProgress")
data class FileDownloadProgressEventData(
  override val displayName: String,
  override val eventTime: Long,
  override val descriptor: FileDownloadOperationDescriptorData? = null,
) : ProgressEventData {
  constructor(event: FileDownloadProgressEvent) : this(
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
) : ProgressEventData {
  constructor(event: FileDownloadStartEvent) : this(
    displayName = event.displayName,
    eventTime = event.eventTime,
    descriptor = FileDownloadOperationDescriptorData(event.descriptor),
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
@SerialName("ProjectConfigurationFinish")
data class ProjectConfigurationFinishEventData(
  override val displayName: String,
  override val eventTime: Long,
  override val descriptor: ProjectConfigurationOperationDescriptorData? = null,
) : ProgressEventData {
  constructor(event: ProjectConfigurationFinishEvent) : this(
    displayName = event.displayName,
    eventTime = event.eventTime,
    descriptor = ProjectConfigurationOperationDescriptorData(event.descriptor),
  )
}

//@Serializable
//@SerialName("ProjectConfigurationProgress")
//data class ProjectConfigurationProgressEventData(
//  override val displayName: String,
//  override val eventTime: Long,
//  override val descriptor: ProjectConfigurationOperationDescriptorData? = null,
//) : ProgressEventData {
//  constructor(event: ProjectConfigurationProgressEvent) : this(
//    displayName = event.displayName,
//    eventTime = event.eventTime,
//    descriptor = ProjectConfigurationOperationDescriptorData(event.descriptor),
//  )
//}

@Serializable
@SerialName("ProjectConfigurationStart")
data class ProjectConfigurationStartEventData(
  override val displayName: String,
  override val eventTime: Long,
  override val descriptor: ProjectConfigurationOperationDescriptorData?,
) : ProgressEventData {
  constructor(event: ProjectConfigurationStartEvent) : this(
    displayName = event.displayName,
    eventTime = event.eventTime,
    descriptor = ProjectConfigurationOperationDescriptorData(event.descriptor),
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

@Serializable
@SerialName("TaskFinish")
data class TaskFinishEventData(
  override val displayName: String,
  override val eventTime: Long,
  override val descriptor: TaskOperationDescriptorData? = null,
) : ProgressEventData {
  constructor(event: TaskFinishEvent) : this(
    displayName = event.displayName,
    eventTime = event.eventTime,
    descriptor = TaskOperationDescriptorData(event.descriptor),
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

@Serializable
@SerialName("TestFinish")
data class TestFinishEventData(
  override val displayName: String,
  override val eventTime: Long,
  override val descriptor: TestOperationDescriptorData? = null,
) : ProgressEventData {
  constructor(event: TestFinishEvent) : this(
    displayName = event.displayName,
    eventTime = event.eventTime,
    descriptor = TestOperationDescriptorData(event.descriptor),
  )
}

@Serializable
@SerialName("TestOutput")
data class TestOutputEventData(
  override val displayName: String,
  override val eventTime: Long,
  override val descriptor: TestOutputDescriptorData? = null,
) : ProgressEventData {
  constructor(event: TestOutputEvent) : this(
    displayName = event.displayName,
    eventTime = event.eventTime,
    descriptor = TestOutputDescriptorData(event.descriptor),
  )
}

//@Serializable
//@SerialName("TestProgress")
//data class TestProgressEventData(
//  override val displayName: String,
//  override val eventTime: Long,
//  override val descriptor: TestProgressEventData? = null,
//) : ProgressEventData {
//  constructor(event: TestProgressEvent) : this(
//    displayName = event.displayName,
//    eventTime = event.eventTime,
//    descriptor = TestProgressEventData(event.descriptor),
//  )
//}

@Serializable
@SerialName("TestStart")
data class TestStartEventData(
  override val displayName: String,
  override val eventTime: Long,
  override val descriptor: OperationDescriptorData? = null,
) : ProgressEventData {
  constructor(event: TestStartEvent) : this(
    displayName = event.displayName,
    eventTime = event.eventTime,
    descriptor = OperationDescriptorData(event.descriptor),
  )
}

@Serializable
@SerialName("TransformFinish")
data class TransformFinishEventData(
  override val displayName: String,
  override val eventTime: Long,
  override val descriptor: TransformOperationDescriptorData? = null,
) : ProgressEventData {
  constructor(event: TransformFinishEvent) : this(
    displayName = event.displayName,
    eventTime = event.eventTime,
    descriptor = TransformOperationDescriptorData(event.descriptor),
  )
}

@Serializable
@SerialName("TransformProgress")
data class TransformProgressEventData(
  override val displayName: String,
  override val eventTime: Long,
  override val descriptor: TransformOperationDescriptorData? = null,
) : ProgressEventData {
  constructor(event: TransformProgressEvent) : this(
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
) : ProgressEventData {
  constructor(event: TransformStartEvent) : this(
    displayName = event.displayName,
    eventTime = event.eventTime,
    descriptor = TransformOperationDescriptorData(event.descriptor),
  )
}

@Serializable
@SerialName("WorkItemFinish")
data class WorkItemFinishEventData(
  override val displayName: String,
  override val eventTime: Long,
  override val descriptor: WorkItemOperationDescriptorData? = null,
) : ProgressEventData {
  constructor(event: WorkItemFinishEvent) : this(
    displayName = event.displayName,
    eventTime = event.eventTime,
    descriptor = WorkItemOperationDescriptorData(event.descriptor),
  )
}

@Serializable
@SerialName("WorkItemProgress")
data class WorkItemProgressEventData(
  override val displayName: String,
  override val eventTime: Long,
  override val descriptor: WorkItemOperationDescriptorData? = null,
) : ProgressEventData {
  constructor(event: WorkItemProgressEvent) : this(
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
) : ProgressEventData {
  constructor(event: WorkItemStartEvent) : this(
    displayName = event.displayName,
    eventTime = event.eventTime,
    descriptor = WorkItemOperationDescriptorData(event.descriptor),
  )
}
