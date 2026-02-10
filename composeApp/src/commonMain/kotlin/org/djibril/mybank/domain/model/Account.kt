package org.djibril.mybank.domain.model

data class Account(
    val label: String,
    val balance: String,
    val operations: List<Operation>
)