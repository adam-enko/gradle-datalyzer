// SPDX-FileCopyrightText: © 2024 JetBrains s.r.o.
// SPDX-License-Identifier: Apache-2.0
package org.jetbrains.experimental.gradle.datalyzer.utils

import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.microseconds
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.DurationUnit.*

/**
 * Truncates the duration to the specified [unit].
 *
 * Truncating the duration removes any time units smaller than the specified unit
 * and returns a new [Duration] with the truncated value.
 *
 * ```kotlin
 * val duration = 1.hours + 30.minutes + 45.seconds + 2.milliseconds
 * println(duration)                                    // 1h 30m 45.002s
 * println(duration.truncate(DurationUnit.NANOSECONDS)) // 1h 30m 45.002s (no truncation)
 * println(duration.truncate(DurationUnit.SECONDS))     // 1h 30m 45s     (milliseconds are truncated)
 * println(duration.truncate(DurationUnit.MINUTES))     // 1h 30m         (seconds are truncated)
 * println(duration.truncate(DurationUnit.HOURS))       // 1h             (minutes are truncated)
 * ```
 *
 * @param unit The duration unit to truncate to.
 * @returns a new [Duration] truncated to the specified [unit].
 */
// https://youtrack.jetbrains.com/issue/KT-60217
internal fun Duration.truncate(unit: DurationUnit): Duration {
  return toComponents { days: Long, hours: Int, minutes: Int, seconds: Int, nanoseconds: Int ->
    when (unit) {
      NANOSECONDS  -> this // there's no smaller unit than NANOSECONDS, so just return the current Duration
      MICROSECONDS -> days.days + hours.hours + minutes.minutes + seconds.seconds + nanoseconds.nanoseconds.inWholeSeconds.seconds + nanoseconds.nanoseconds.inWholeMicroseconds.microseconds
      MILLISECONDS -> days.days + hours.hours + minutes.minutes + seconds.seconds + nanoseconds.nanoseconds.inWholeSeconds.seconds + nanoseconds.nanoseconds.inWholeMilliseconds.milliseconds
      SECONDS      -> days.days + hours.hours + minutes.minutes + seconds.seconds
      MINUTES      -> days.days + hours.hours + minutes.minutes
      HOURS        -> days.days + hours.hours
      DAYS         -> days.days
    }
  }
}
