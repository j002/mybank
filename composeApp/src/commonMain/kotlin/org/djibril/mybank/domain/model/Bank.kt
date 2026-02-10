package org.djibril.mybank.domain.model

data class Bank(
    val name: String,
    val isCa: Boolean,
    val accounts: List<Account>
)