package org.djibril.mybank.utils

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object DateUtils {
    fun formatEpochSeconds(epochSeconds: Long): String {
        val dt = Instant.fromEpochSeconds(epochSeconds)
            .toLocalDateTime(TimeZone.currentSystemDefault())

        val monthFr = when (dt.monthNumber) {
            1 -> "janv."
            2 -> "févr."
            3 -> "mars"
            4 -> "avr."
            5 -> "mai"
            6 -> "juin"
            7 -> "juil."
            8 -> "août"
            9 -> "sept."
            10 -> "oct."
            11 -> "nov."
            12 -> "déc."
            else -> ""
        }
        return "${dt.dayOfMonth} $monthFr ${dt.year}"
    }

    fun normalizeToEpochSeconds(value: Long): Long {
        return if (value > 9_999_999_999L) value / 1000 else value
    }
}
