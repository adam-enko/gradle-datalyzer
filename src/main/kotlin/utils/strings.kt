// SPDX-FileCopyrightText: © 2024 JetBrains s.r.o.
// SPDX-License-Identifier: Apache-2.0
package org.jetbrains.experimental.gradle.datalyzer.utils


internal fun String.replaceNonAlphaNumeric(replacement: String = "-"): String =
  map { if (it.isLetterOrDigit()) it else replacement }.joinToString("")
