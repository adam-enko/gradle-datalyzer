package org.jetbrains.experimental.gpde.utils


internal fun String.replaceNonAlphaNumeric(replacement: String = "-"): String =
  map { if (it.isLetterOrDigit()) it else replacement }.joinToString("")
