package org.jetbrains.experimental.gpde

import com.github.ajalt.mordant.terminal.Terminal
import org.jetbrains.experimental.gpde.utils.replaceNonAlphaNumeric
import java.io.BufferedOutputStream
import java.io.BufferedWriter
import java.io.File
import java.nio.file.Path
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.io.path.*

internal class Reporter(
  private val outputDir: Path,
  private val terminal: Terminal,
) {
  private val scripts = outputDir.resolve("scripts.md")
  private val log = outputDir.resolve("output.log")

  init {
    outputDir.createDirectories()
    scripts.createFile()
    log.createFile()
  }

  private val logWriter = log.bufferedWriter()

  fun collectScript(
    projectName: String,
    script: File?,
  ) {
    scripts.bufferedWriter().use { writer ->

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
    }
  }


  fun taskOutput(taskName: String): BufferedOutputStream {
    return outputDir
      .resolve("task-stdout-${taskName.replaceNonAlphaNumeric()}-${System.currentTimeMillis()}.txt")
      .createFile()
      .outputStream()
      .buffered()
  }

  fun taskData(taskName: String): BufferedWriter {
    return outputDir
      .resolve("task-data-${taskName.replaceNonAlphaNumeric()}-${System.currentTimeMillis()}.txt")
      .createFile()
      .bufferedWriter()
  }


  fun log(msg: String) {
    terminal.muted(msg)
    logWriter.appendLine(msg)
    logWriter.flush()
  }

  fun zip() {
    val zipFile = outputDir.parent.resolve("${outputDir.name}.zip").apply {
      if (exists()) deleteExisting()
      createFile()
    }

    ZipOutputStream(zipFile.outputStream().buffered()).use { zip ->
      outputDir.walk().forEach { src ->
        val zipFileName = src.absolute().invariantSeparatorsPathString
          .removePrefix(outputDir.absolute().invariantSeparatorsPathString)
          .removePrefix("/")
          .let { if (src.isDirectory()) "$it/" else it }

        val entry = ZipEntry(zipFileName)
        zip.putNextEntry(entry)

        if (src.isRegularFile()) {
          src.inputStream().use { it.copyTo(zip) }
        }
      }
    }
  }
}
