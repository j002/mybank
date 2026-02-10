package org.djibril.mybank.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class BankDto(
    val name: String,
    val isCA: Int,
    val accounts: List<AccountDto>
)
