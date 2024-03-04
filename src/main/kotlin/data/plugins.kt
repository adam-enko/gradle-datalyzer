package org.jetbrains.experimental.gpde.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.gradle.tooling.events.BinaryPluginIdentifier
import org.gradle.tooling.events.PluginIdentifier
import org.gradle.tooling.events.ScriptPluginIdentifier
import org.gradle.tooling.events.configuration.ProjectConfigurationOperationResult


fun PluginIdentifierData(ident: PluginIdentifier?): PluginIdentifierData? {
  return when (ident) {
    is ScriptPluginIdentifier -> PluginIdentifierData.Script(ident)
    is BinaryPluginIdentifier -> PluginIdentifierData.Binary(ident)
    else                      -> null
  }
}

@Serializable
sealed interface PluginIdentifierData {
  val displayName: String

  @Serializable
  @SerialName("Binary")
  data class Binary(
    override val displayName: String,
    val pluginId: String?,
    val className: String,
  ) : PluginIdentifierData {
    constructor(ident: BinaryPluginIdentifier) : this(
      displayName = ident.displayName,
      pluginId = ident.pluginId,
      className = ident.className,
    )
  }

  @Serializable
  @SerialName("Script")
  data class Script(
    override val displayName: String,
    val uri: String,
//    val content: String,
  ) : PluginIdentifierData {
    constructor(ident: ScriptPluginIdentifier) : this(
      displayName = ident.displayName,
      uri = ident.uri.toString(),
//      content = ident.uri,
    )
  }
}


@Serializable
data class PluginApplicationResultData(
  val plugin: PluginIdentifierData?,
  val totalConfigurationTimeMillis: Long,
) {
  constructor(result: ProjectConfigurationOperationResult.PluginApplicationResult) : this(
    plugin = PluginIdentifierData(result.plugin),
    totalConfigurationTimeMillis = result.totalConfigurationTime.toMillis(),
  )
}
