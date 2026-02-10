package org.djibril.mybank.ui.banks

import org.djibril.mybank.domain.model.Bank

data class BanksUiState(
    val loading: Boolean = false,
    val caBanks: List<Bank> = emptyList(),
    val otherBanks: List<Bank> = emptyList(),
    val expandedBankNames: Set<String> = emptySet(),
    val errorMessage: String? = null
)
