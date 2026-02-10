package org.djibril.mybank.utils

object AmountUtils {
    fun parseToDouble(raw: String): Double {
        // Supporte: "-45,99", "-45.99", " -45,99 € ", "+12,00"
        val cleaned = raw
            .trim()
            .replace("€", "")
            .replace(" ", "")
            .replace(",", ".")
        return cleaned.toDoubleOrNull() ?: 0.0
    }

    fun formatFr(value: Double, currency: String): String {
        val sign = if (value < 0) "-" else ""
        val absValue = kotlin.math.abs(value)

        val euros = absValue.toInt()
        val cents = ((absValue - euros) * 100)
            .toInt()
            .toString()
            .padStart(2, '0')

        return "$sign$euros,$cents $currency"
    }
}
