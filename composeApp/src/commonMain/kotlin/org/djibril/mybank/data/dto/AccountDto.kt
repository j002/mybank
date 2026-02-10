package org.djibril.mybank.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class AccountDto(
    val label: String,
    val balance: String,
    val operations: List<OperationDto>
)
