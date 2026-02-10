package org.djibril.mybank.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class OperationDto(
    val title: String,
    val date: Long,
    val amount: String
)

