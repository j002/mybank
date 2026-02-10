package org.djibril.mybank.utils

import org.djibril.mybank.domain.model.Bank
import org.djibril.mybank.domain.model.Operation

object BankSorter {

    fun sort(banks: List<Bank>): List<Bank> =
        banks.sortedWith(
            compareBy { it.name.lowercase() }
        )
}

object OperationSorter {

    fun sort(operations: List<Operation>): List<Operation> =
        operations.sortedWith(
            compareByDescending<Operation> { it.epochSeconds }
                .thenBy { it.title.lowercase() }
        )
}

