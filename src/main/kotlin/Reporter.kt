package org.jetbrains.experimental.gpde

import java.io.File
import java.nio.file.Path
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.io.path.*

internal class Reporter(
  private val outputDir: Path
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


  fun log(msg: String) {
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
