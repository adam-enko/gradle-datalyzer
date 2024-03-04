package org.jetbrains.experimental.gpde.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.gradle.tooling.events.OperationDescriptor
import org.gradle.tooling.events.configuration.ProjectConfigurationOperationDescriptor
import org.gradle.tooling.events.download.FileDownloadOperationDescriptor
import org.gradle.tooling.events.lifecycle.BuildPhaseOperationDescriptor
import org.gradle.tooling.events.problems.*
import org.gradle.tooling.events.problems.internal.DefaultProblemsOperationDescriptor
import org.gradle.tooling.events.task.TaskOperationDescriptor
import org.gradle.tooling.events.test.*
import org.gradle.tooling.events.transform.TransformOperationDescriptor
import org.gradle.tooling.events.work.WorkItemOperationDescriptor


@Suppress("UnstableApiUsage")
fun OperationDescriptorData(
  desc: OperationDescriptor?
): OperationDescriptorData? {
  return when (desc) {
    null                                       -> null
    is BuildPhaseOperationDescriptor           -> BuildPhaseOperationDescriptorData(desc)
    is FileDownloadOperationDescriptor         -> FileDownloadOperationDescriptorData(desc)
    is ProblemAggregationDescriptor            -> ProblemAggregationDescriptorData(desc)
    is DefaultProblemsOperationDescriptor      -> ProblemDescriptorData(desc)
    is ProjectConfigurationOperationDescriptor -> ProjectConfigurationOperationDescriptorData(desc)
    is TaskOperationDescriptor                 -> TaskOperationDescriptorData(desc)
    is TestOutputDescriptor                    -> TestOutputDescriptorData(desc)
    is TransformOperationDescriptor            -> TransformOperationDescriptorData(desc)
    is WorkItemOperationDescriptor             -> WorkItemOperationDescriptorData(desc)

    is JvmTestOperationDescriptor              -> JvmTestOperationDescriptorData(desc)
    is TestOperationDescriptor                 -> TestOperationDescriptorData(desc)

    else                                       -> GeneralDescriptorData(desc)
  }
}

@Serializable
sealed interface OperationDescriptorData {
  val name: String
  val displayName: String
  val parentName: String?
}


@Serializable
@SerialName("General")
data class GeneralDescriptorData(
  override val name: String,
  override val displayName: String,
  override val parentName: String? = null,
) : OperationDescriptorData {
  constructor(desc: OperationDescriptor) : this(
    name = desc.name,
    displayName = desc.displayName,
    parentName = desc.parent?.name,
  )
}


@Serializable
@SerialName("BuildPhaseOperation")
data class BuildPhaseOperationDescriptorData(
  override val name: String,
  override val displayName: String,
  override val parentName: String? = null,
  val buildPhase: String,
  val buildItemsCount: Int,
) : OperationDescriptorData {
  constructor(desc: BuildPhaseOperationDescriptor) : this(
    name = desc.name,
    displayName = desc.displayName,
    parentName = desc.parent?.name,
    buildPhase = desc.buildPhase,
    buildItemsCount = desc.buildItemsCount,
  )
}


@Serializable
@SerialName("FileDownloadOperation")
data class FileDownloadOperationDescriptorData(
  override val name: String,
  override val displayName: String,
  override val parentName: String? = null,
  val uri: String?,
) : OperationDescriptorData {
  constructor(desc: FileDownloadOperationDescriptor) : this(
    name = desc.name,
    displayName = desc.displayName,
    parentName = desc.parent?.name,
    uri = desc.uri?.toString(),
  )
}


fun BaseProblemDescriptorData(base: BaseProblemDescriptor?): BaseProblemDescriptorData? {
  return when (base) {
    is ProblemAggregationDescriptor       -> ProblemAggregationDescriptorData(base)
    is DefaultProblemsOperationDescriptor -> ProblemDescriptorData(base)
    else                                  -> null
  }
}

@Serializable
sealed interface BaseProblemDescriptorData : OperationDescriptorData

@Serializable
@SerialName("ProblemAggregation")
data class ProblemAggregationDescriptorData(
  override val name: String,
  override val displayName: String,
  override val parentName: String? = null,
  val aggregations: List<ProblemAggregationData>? = null,
) : BaseProblemDescriptorData {
  constructor(desc: ProblemAggregationDescriptor) : this(
    name = desc.name,
    displayName = desc.displayName,
    parentName = desc.parent?.name,
    aggregations = desc.aggregations.map { ProblemAggregationData(it) }
  )

  @Serializable
  data class ProblemAggregationData(
    val categoryData: ProblemCategoryData,
    val label: String,
    val problemDescriptors: List<ProblemDescriptorData>,
  ) {
    constructor(aggregation: ProblemAggregation) : this(
      categoryData = ProblemCategoryData(aggregation.category),
      label = aggregation.label.label,
      problemDescriptors = aggregation.problemDescriptors.map(::ProblemDescriptorData),
    )
  }

  @Serializable
  data class ProblemCategoryData(
    val namespace: String? = null,
    val category: String? = null,
    val subcategories: List<String>? = null,
  ) {
    constructor(category: ProblemCategory) : this(
      namespace = category.namespace,
      category = category.category,
      subcategories = category.subcategories
    )
  }
}


@Serializable
@SerialName("Problem")
data class ProblemDescriptorData(
  override val name: String,
  override val displayName: String,
  override val parentName: String? = null,
  val category: ProblemAggregationDescriptorData.ProblemCategoryData?,
  val label: String? = null,
  val details: String? = null,
  val severity: Int,
) : BaseProblemDescriptorData {
  constructor(desc: ProblemDescriptor) : this(
    name = desc.name,
    displayName = desc.displayName,
    parentName = desc.parent?.name,
    category = desc.category?.let(ProblemAggregationDescriptorData::ProblemCategoryData),
    label = desc.label?.label,
    details = desc.details?.details,
    severity = desc.severity.severity,
  )
}


@Serializable
@SerialName("ProjectConfigurationOperation")
data class ProjectConfigurationOperationDescriptorData(
  override val name: String,
  override val displayName: String,
  override val parentName: String? = null,
  val project: ProjectIdentifierData,
) : OperationDescriptorData {
  constructor(desc: ProjectConfigurationOperationDescriptor) : this(
    name = desc.name,
    displayName = desc.displayName,
    parentName = desc.parent?.name,
    project = ProjectIdentifierData(desc.project),
  )
}


@Serializable
@SerialName("TaskOperation")
data class TaskOperationDescriptorData(
  override val name: String,
  override val displayName: String,
  override val parentName: String? = null,
  val taskPath: String?,
  val originPlugin: PluginIdentifierData?,
) : OperationDescriptorData {
  constructor(desc: TaskOperationDescriptor) : this(
    name = desc.name,
    displayName = desc.displayName,
    parentName = desc.parent?.name,
    taskPath = desc.taskPath,
    originPlugin = PluginIdentifierData(desc.originPlugin),
  )
}


@Serializable
sealed interface TestOperationDescriptorData : OperationDescriptorData

fun TestOperationDescriptorData(data: TestOperationDescriptor): TestOperationDescriptorData {
  return when (data) {
    is JvmTestOperationDescriptor -> JvmTestOperationDescriptorData(data)
    else                          -> DefaultTestOperationDescriptorData(data)
  }
}


@Serializable
@SerialName("DefaultTestOperation")
data class DefaultTestOperationDescriptorData(
  override val name: String,
  override val displayName: String,
  override val parentName: String? = null,
) : TestOperationDescriptorData {
  constructor(desc: TestOperationDescriptor) : this(
    name = desc.name,
    displayName = desc.displayName,
    parentName = desc.parent?.name,
  )
}

@Serializable
@SerialName("JvmTestOperation")
data class JvmTestOperationDescriptorData(
  override val name: String,
  override val displayName: String,
  override val parentName: String? = null,
  val jvmTestKind: JvmTestKind,
  val suiteName: String? = null,
  val className: String? = null,
  val methodName: String? = null,
) : TestOperationDescriptorData {
  constructor(desc: JvmTestOperationDescriptor) : this(
    name = desc.name,
    displayName = desc.displayName,
    parentName = desc.parent?.name,
    jvmTestKind = desc.jvmTestKind,
    suiteName = desc.suiteName,
    className = desc.className,
    methodName = desc.methodName,
  )
}

@Serializable
@SerialName("TestOutput")
data class TestOutputDescriptorData(
  override val name: String,
  override val displayName: String,
  override val parentName: String? = null,
  val destination: Destination?,
  val message: String?,
) : TestOperationDescriptorData {
  constructor(desc: TestOutputDescriptor) : this(
    name = desc.name,
    displayName = desc.displayName,
    parentName = desc.parent?.name,
    destination = desc.destination,
    message = desc.message,
  )
}


@Serializable
@SerialName("TransformOperation")
data class TransformOperationDescriptorData(
  override val name: String,
  override val displayName: String,
  override val parentName: String? = null,
  val transformer: String? = null,
  val subject: String? = null,
  val dependencies: List<OperationDescriptorData?>?,
) : OperationDescriptorData {
  constructor(desc: TransformOperationDescriptor) : this(
    name = desc.name,
    displayName = desc.displayName,
    parentName = desc.parent?.name,
    transformer = desc.transformer?.displayName,
    subject = desc.subject?.displayName,
    dependencies = desc.dependencies?.map { OperationDescriptorData(it) }
  )
}


@Serializable
@SerialName("WorkItemOperation")
data class WorkItemOperationDescriptorData(
  override val name: String,
  override val displayName: String,
  override val parentName: String? = null,
  val className: String? = null,
) : OperationDescriptorData {
  constructor(desc: WorkItemOperationDescriptor) : this(
    name = desc.name,
    displayName = desc.displayName,
    parentName = desc.parent?.name,
    className = desc.className,
  )
}
