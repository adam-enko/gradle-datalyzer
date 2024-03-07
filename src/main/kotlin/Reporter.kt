// SPDX-FileCopyrightText: © 2024 JetBrains s.r.o.
// SPDX-License-Identifier: Apache-2.0
package org.jetbrains.experimental.gradle.datalyzer

import com.github.ajalt.mordant.terminal.Terminal
import org.jetbrains.experimental.gradle.datalyzer.utils.replaceNonAlphaNumeric
import java.io.BufferedOutputStream
import java.io.BufferedWriter
import java.io.File
import java.nio.file.Path
import java.nio.file.StandardOpenOption.APPEND
import java.nio.file.StandardOpenOption.SYNC
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.io.path.*

/**
 * Gather information about the Gradle project, and save it to disk.
 */
internal class Reporter(
  reportsDir: Path,
  private val terminal: Terminal,
) {
  private val reportZip = reportsDir.resolve("report.zip")
  private val dataDir = reportsDir.resolve("data")

  private val scripts = dataDir.resolve("scripts.md")
  private val log = dataDir.resolve("output.log")

  init {
    reportsDir.createDirectories()
    dataDir.createDirectories()
    scripts.createFile()
    log.createFile()
  }

  private val logWriter = log.bufferedWriter()

  fun collectScript(
    projectName: String,
    script: File?,
  ) {
    scripts.bufferedWriter(options = arrayOf(APPEND, SYNC)).use { writer ->

      val contents = if (script?.exists() == true) {
        """
        |
        |### $projectName
        |
        |${script.absoluteFile.canonicalFile.invariantSeparatorsPath}
        |
        |```
        |${script.readText()}
        |```
        |
        """.trimMargin()
      } else {
        """
        |
        |### $projectName
        |
        |script not found
        |
        """.trimMargin()
      }

      writer.appendLine(contents)
      writer.flush()
    }
  }


  fun taskOutput(taskName: String): BufferedOutputStream {
    return dataDir
      .resolve("task-stdout-${taskName.replaceNonAlphaNumeric()}-${System.currentTimeMillis()}.txt")
      .createFile()
      .outputStream()
      .buffered()
  }

  fun taskData(taskName: String): BufferedWriter {
    return dataDir
      .resolve("task-data-${taskName.replaceNonAlphaNumeric()}-${System.currentTimeMillis()}.txt")
      .createFile()
      .bufferedWriter()
  }

  fun logInfo(msg: String) {
    terminal.info(msg)
    logWriter.appendLine(msg)
    logWriter.flush()
  }

  fun logWarning(msg: String) {
    terminal.warning(msg)
    logWriter.appendLine(msg)
    logWriter.flush()
  }

  fun log(msg: String) {
    terminal.muted(msg)
    logWriter.appendLine(msg)
    logWriter.flush()
  }

  fun zip(): Path {
    val zipFile = reportZip.apply {
      if (exists()) deleteExisting()
      createFile()
    }

    ZipOutputStream(zipFile.outputStream().buffered()).use { zip ->
      dataDir.walk().forEach { src ->
        val zipFileName = src.absolute().invariantSeparatorsPathString
          .removePrefix(dataDir.absolute().invariantSeparatorsPathString)
          .removePrefix("/")
          .let { if (src.isDirectory()) "$it/" else it }

        val entry = ZipEntry(zipFileName)
        zip.putNextEntry(entry)

        if (src.isRegularFile()) {
          src.inputStream().use { it.copyTo(zip) }
        }
      }
    }

    return zipFile
  }
}
