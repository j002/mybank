package org.djibril.mybank.ui.operations

import org.djibril.mybank.domain.model.Operation

data class OperationsUiState(
    val bankName: String,
    val accountLabel: String,
    val operations: List<Operation> = emptyList()
)
