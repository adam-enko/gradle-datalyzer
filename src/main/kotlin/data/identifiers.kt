package org.jetbrains.experimental.gpde.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.gradle.tooling.model.BuildIdentifier
import org.gradle.tooling.model.ProjectIdentifier

@Serializable
@SerialName("ProjectIdentifier")
data class ProjectIdentifierData(
  val projectPath: String,
  val buildIdentifier: BuildIdentifierData,
) {
  constructor(ident: ProjectIdentifier) : this(
    projectPath = ident.projectPath,
    buildIdentifier = BuildIdentifierData(ident.buildIdentifier),
  )
}

@Serializable
@SerialName("BuildIdentifier")
data class BuildIdentifierData(
  val rootDir: String,
) {
  constructor(ident: BuildIdentifier) : this(
    rootDir = ident.rootDir.invariantSeparatorsPath
  )
}
