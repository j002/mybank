package org.djibril.mybank.domain.model

import org.djibril.mybank.utils.AmountUtils
import org.djibril.mybank.utils.DateUtils


data class Operation(
    val title: String,
    val epochSeconds: Long,
    val amount: Double,
    val currency: String
) {
    val isNegative: Boolean get() = amount < 0

    val formattedAmount: String
        get() = AmountUtils.formatFr(amount, currency)

    val formattedDate: String
        get() = DateUtils.formatEpochSeconds(epochSeconds)
}


