package org.djibril.mybank.data.mapper

import org.djibril.mybank.data.dto.AccountDto
import org.djibril.mybank.data.dto.BankDto
import org.djibril.mybank.data.dto.OperationDto
import org.djibril.mybank.domain.model.Account
import org.djibril.mybank.domain.model.Bank
import org.djibril.mybank.domain.model.Operation
import org.djibril.mybank.utils.AmountUtils
import org.djibril.mybank.utils.DateUtils
import org.djibril.mybank.utils.OperationSorter

class BankMapper() {

    fun map(dto: BankDto): Bank =
        Bank(
            name = dto.name,
            isCa = dto.isCA == 1,
            accounts = dto.accounts.map { map(it) }
        )

    private fun map(dto: AccountDto): Account =
        Account(
            label = dto.label,
            balance = dto.balance,
            operations = OperationSorter.sort(
                dto.operations.map { map(it) }
            )
        )

    private fun map(dto: OperationDto): Operation {
        val epochSeconds = DateUtils.normalizeToEpochSeconds(dto.date)

        return Operation(
            title = dto.title,
            epochSeconds = epochSeconds,
            amount = AmountUtils.parseToDouble(dto.amount),
            currency = "€"
        )
    }

}
