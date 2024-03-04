package org.jetbrains.experimental.gpde.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.gradle.tooling.Failure
import org.gradle.tooling.events.OperationResult
import org.gradle.tooling.events.SkippedResult
import org.gradle.tooling.events.SuccessResult
import org.gradle.tooling.events.configuration.ProjectConfigurationFailureResult
import org.gradle.tooling.events.configuration.ProjectConfigurationOperationResult
import org.gradle.tooling.events.configuration.ProjectConfigurationSuccessResult
import org.gradle.tooling.events.download.FileDownloadNotFoundResult
import org.gradle.tooling.events.download.FileDownloadResult
import org.gradle.tooling.events.task.TaskFailureResult
import org.gradle.tooling.events.task.TaskOperationResult
import org.gradle.tooling.events.task.TaskSkippedResult
import org.gradle.tooling.events.task.TaskSuccessResult
import org.gradle.tooling.events.task.java.JavaCompileTaskOperationResult
import org.gradle.tooling.events.test.TestFailureResult
import org.gradle.tooling.events.test.TestOperationResult
import org.gradle.tooling.events.test.TestSkippedResult
import org.gradle.tooling.events.test.TestSuccessResult
import org.gradle.tooling.events.transform.TransformFailureResult
import org.gradle.tooling.events.transform.TransformOperationResult
import org.gradle.tooling.events.transform.TransformSuccessResult
import org.gradle.tooling.events.work.WorkItemFailureResult
import org.gradle.tooling.events.work.WorkItemOperationResult
import org.gradle.tooling.events.work.WorkItemSuccessResult
import org.jetbrains.experimental.gpde.data.FailureResultData.FailureData


@Suppress("UnstableApiUsage")
fun OperationResultData(result: OperationResult): OperationResultData? {
  return when (result) {
    is TaskFailureResult                 -> TaskFailureResultData(result)
    is TaskSkippedResult                 -> TaskSkippedResultData(result)
    is TaskSuccessResult                 -> TaskSuccessResultData(result)

    is TestSkippedResult                 -> TestSkippedResultData(result)
    is TestSuccessResult                 -> TestSuccessResultData(result)

    is TransformSuccessResult            -> TransformSuccessResultData(result)
    is WorkItemSuccessResult             -> WorkItemSuccessResultData(result)

    is FileDownloadNotFoundResult        -> FileDownloadNotFoundResultData(result)
    is JavaCompileTaskOperationResult    -> JavaCompileTaskOperationResultData(result)

    is ProjectConfigurationSuccessResult -> ProjectConfigurationSuccessResultData(result)
    is ProjectConfigurationFailureResult -> ProjectConfigurationFailureResultData(result)
//    is ProjectConfigurationOperationResult -> ProjectConfigurationOperationResultData(result)

    is SkippedResult                     -> SkippedResultData(result)
    is SuccessResult                     -> SuccessResultData(result)
    is TaskOperationResult               -> TaskOperationResultData(result)
//    is TaskOperationResult                 -> TaskOperationResultData(result)
    is TestFailureResult                 -> TestFailureResultData(result)
//    is TestOperationResult                 -> TestOperationResultData(result)
    is TransformFailureResult            -> TransformFailureResultData(result)
    is TransformOperationResult          -> TransformOperationResultData(result)
    is WorkItemFailureResult             -> WorkItemFailureResultData(result)
    is WorkItemOperationResult           -> WorkItemOperationResultData(result)

    is FileDownloadResult                -> FileDownloadResultData(result)
//    is FailureResult                       -> FailureResultData(result)

    else                                 -> null
  }
}

@Serializable
sealed interface OperationResultData {
  val startTime: Long
  val endTime: Long
}

@Serializable
sealed interface FailureResultData : OperationResultData {
  val failures: List<FailureData>

  @Serializable
  data class FailureData(
    val message: String? = null,
    val description: String? = null,
    val causes: List<FailureData>,
  ) {
    constructor(failure: Failure) : this(
      message = failure.message,
      description = failure.description,
      causes = failure.causes.map { FailureData(it) }
    )
  }
}


fun ProjectConfigurationOperationResultData(
  result: ProjectConfigurationOperationResult
): ProjectConfigurationOperationResultData? {
  return when (result) {
    is ProjectConfigurationFailureResult -> ProjectConfigurationFailureResultData(result)
    is ProjectConfigurationSuccessResult -> ProjectConfigurationSuccessResultData(result)
    else                                 -> null
  }
}


@Serializable
sealed interface ProjectConfigurationOperationResultData : OperationResultData {
  val pluginApplications: List<PluginApplicationResultData>
}


@Serializable
@SerialName("ProjectConfigurationFailure")
data class ProjectConfigurationFailureResultData(
  override val startTime: Long,
  override val endTime: Long,
  override val failures: List<FailureData>,
  override val pluginApplications: List<PluginApplicationResultData>,
) : ProjectConfigurationOperationResultData, FailureResultData {
  constructor(result: ProjectConfigurationFailureResult) : this(
    startTime = result.startTime,
    endTime = result.endTime,
    failures = result.failures.map { FailureData(it) },
    pluginApplications = result.pluginApplicationResults.map { PluginApplicationResultData(it) },
  )
}


@Serializable
@SerialName("ProjectConfigurationSuccess")
data class ProjectConfigurationSuccessResultData(
  override val startTime: Long,
  override val endTime: Long,
  override val pluginApplications: List<PluginApplicationResultData>,
) : ProjectConfigurationOperationResultData {
  constructor(result: ProjectConfigurationSuccessResult) : this(
    startTime = result.startTime,
    endTime = result.endTime,
    pluginApplications = result.pluginApplicationResults.map { PluginApplicationResultData(it) },
  )
}


//@Serializable
//data class FailureResultData(
//  override val startTime: Long,
//  override val endTime: Long,
//) : OperationResultData {
//  constructor(result: FailureResult) : this(
//    result.startTime,
//    result.endTime,
//  )
//}

@Serializable
@SerialName("FileDownloadNotFound")
data class FileDownloadNotFoundResultData(
  override val startTime: Long,
  override val endTime: Long,
  val bytesDownloaded: Long,
) : OperationResultData {
  constructor(result: FileDownloadNotFoundResult) : this(
    startTime = result.startTime,
    endTime = result.endTime,
    bytesDownloaded = result.bytesDownloaded,
  )
}

@Serializable
@SerialName("FileDownload")
data class FileDownloadResultData(
  override val startTime: Long,
  override val endTime: Long,
  val bytesDownloaded: Long,
) : OperationResultData {
  constructor(result: FileDownloadResult) : this(
    startTime = result.startTime,
    endTime = result.endTime,
    bytesDownloaded = result.bytesDownloaded,
  )
}

@Serializable
@SerialName("JavaCompileTaskOperation")
data class JavaCompileTaskOperationResultData(
  override val startTime: Long,
  override val endTime: Long,
  val annotationProcessorResults: List<AnnotationProcessorResultData>,
) : OperationResultData {
  constructor(result: JavaCompileTaskOperationResult) : this(
    startTime = result.startTime,
    endTime = result.endTime,
    annotationProcessorResults = result.annotationProcessorResults.map { AnnotationProcessorResultData(it) }
  )

  @Serializable
  data class AnnotationProcessorResultData(
    val className: String,
    val type: String,
    val durationMillis: Long,
  ) {
    constructor(result: JavaCompileTaskOperationResult.AnnotationProcessorResult) : this(
      className = result.className,
      type = result.type.toString(),
      durationMillis = result.duration.toMillis(),
    )
  }
}

@Serializable
@SerialName("Skipped")
data class SkippedResultData(
  override val startTime: Long,
  override val endTime: Long,
) : OperationResultData {
  constructor(result: SkippedResult) : this(
    startTime = result.startTime,
    endTime = result.endTime,
  )
}

@Serializable
@SerialName("Success")
data class SuccessResultData(
  override val startTime: Long,
  override val endTime: Long,
) : OperationResultData {
  constructor(result: SuccessResult) : this(
    startTime = result.startTime,
    endTime = result.endTime,
  )
}


@Serializable
sealed interface TaskOperationResultData : OperationResultData

fun TaskOperationResultData(result: TaskOperationResult): TaskOperationResultData? {
  return when (result) {
    is TaskFailureResult -> TaskFailureResultData(result)
    is TaskSkippedResult -> TaskSkippedResultData(result)
    is TaskSuccessResult -> TaskSuccessResultData(result)
    else                 -> null
  }
}

@Serializable
sealed interface TaskExecutionResultData : TaskOperationResultData {
  val isIncremental: Boolean
  val executionReasons: List<String>?
}

@Serializable
@SerialName("TaskFailure")
data class TaskFailureResultData(
  override val startTime: Long,
  override val endTime: Long,
  override val failures: List<FailureData>,
  override val isIncremental: Boolean = false,
  override val executionReasons: List<String>? = null,
) : TaskExecutionResultData, FailureResultData {
  constructor(result: TaskFailureResult) : this(
    startTime = result.startTime,
    endTime = result.endTime,
    failures = result.failures.map { FailureData(it) },
    isIncremental = result.isIncremental,
    executionReasons = result.executionReasons,
  )
}

//@Serializable
//data class TaskOperationResultData(
//  override val startTime: Long,
//  override val endTime: Long,
//) : OperationResultData {
//  constructor(result: TaskOperationResult) : this(
//    startTime = result.startTime,
//    endTime = result.endTime,
//  )
//}

@Serializable
@SerialName("TaskSkipped")
data class TaskSkippedResultData(
  override val startTime: Long,
  override val endTime: Long,
  val skipMessage: String? = null,
) : TaskOperationResultData {
  constructor(result: TaskSkippedResult) : this(
    startTime = result.startTime,
    endTime = result.endTime,
    skipMessage = result.skipMessage,
  )
}

@Serializable
@SerialName("TaskSuccess")
data class TaskSuccessResultData(
  override val startTime: Long,
  override val endTime: Long,
  override val isIncremental: Boolean = false,
  val isUpToDate: Boolean = false,
  val isFromCache: Boolean = false,
  override val executionReasons: List<String>? = null,
) : TaskExecutionResultData {
  constructor(result: TaskSuccessResult) : this(
    startTime = result.startTime,
    endTime = result.endTime,
    isIncremental = result.isIncremental,
    isUpToDate = result.isUpToDate,
    isFromCache = result.isFromCache,
    executionReasons = result.executionReasons,
  )
}

@Serializable
@SerialName("TestFailure")
data class TestFailureResultData(
  override val startTime: Long,
  override val endTime: Long,
  override val failures: List<FailureData>,
) : FailureResultData {
  constructor(result: TestFailureResult) : this(
    startTime = result.startTime,
    endTime = result.endTime,
    failures = result.failures.map { FailureData(it) },
  )
}

@Serializable
sealed interface TestOperationResultData : OperationResultData

fun TestOperationResultData(result: TestOperationResult): TestOperationResultData? {
  return when (result) {
    is TransformFailureResult -> TransformFailureResultData(result)
    is TestSkippedResult      -> TestSkippedResultData(result)
    is TestSuccessResult      -> TestSuccessResultData(result)
    else                      -> null
  }
}

@Serializable
@SerialName("TestSkipped")
data class TestSkippedResultData(
  override val startTime: Long,
  override val endTime: Long,
) : TestOperationResultData {
  constructor(result: TestSkippedResult) : this(
    startTime = result.startTime,
    endTime = result.endTime,
  )
}

@Serializable
@SerialName("TestSuccess")
data class TestSuccessResultData(
  override val startTime: Long,
  override val endTime: Long,
) : TestOperationResultData {
  constructor(result: TestSuccessResult) : this(
    startTime = result.startTime,
    endTime = result.endTime,
  )
}

@Serializable
@SerialName("TransformFailure")
data class TransformFailureResultData(
  override val startTime: Long,
  override val endTime: Long,
  override val failures: List<FailureData>,
) : TestOperationResultData, FailureResultData {
  constructor(result: TransformFailureResult) : this(
    startTime = result.startTime,
    endTime = result.endTime,
    failures = result.failures.map { FailureData(it) },
  )
}

@Serializable
@SerialName("TransformOperation")
data class TransformOperationResultData(
  override val startTime: Long,
  override val endTime: Long,
) : OperationResultData {
  constructor(result: TransformOperationResult) : this(
    startTime = result.startTime,
    endTime = result.endTime,
  )
}

@Serializable
@SerialName("TransformSuccess")
data class TransformSuccessResultData(
  override val startTime: Long,
  override val endTime: Long,
) : OperationResultData {
  constructor(result: TransformSuccessResult) : this(
    startTime = result.startTime,
    endTime = result.endTime,
  )
}

@Serializable
@SerialName("WorkItemFailure")
data class WorkItemFailureResultData(
  override val startTime: Long,
  override val endTime: Long,
  override val failures: List<FailureData>,
) : FailureResultData {
  constructor(result: WorkItemFailureResult) : this(
    startTime = result.startTime,
    endTime = result.endTime,
    failures = result.failures.map { FailureData(it) },
  )
}

@Serializable
@SerialName("WorkItemOperation")
data class WorkItemOperationResultData(
  override val startTime: Long,
  override val endTime: Long,
) : OperationResultData {
  constructor(result: WorkItemOperationResult) : this(
    startTime = result.startTime,
    endTime = result.endTime,
  )
}

@Serializable
@SerialName("WorkItemSuccess")
data class WorkItemSuccessResultData(
  override val startTime: Long,
  override val endTime: Long,
) : OperationResultData {
  constructor(result: WorkItemSuccessResult) : this(
    startTime = result.startTime,
    endTime = result.endTime,
  )
}
