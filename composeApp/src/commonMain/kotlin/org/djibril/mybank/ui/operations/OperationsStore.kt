package org.djibril.mybank.ui.operations

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.djibril.mybank.domain.model.Bank
import org.djibril.mybank.domain.model.Operation

class OperationsStore(
    bankName: String,
    accountLabel: String,
    banks: List<Bank>
) {
    private val ops: List<Operation> =
        banks.firstOrNull { it.name == bankName }
            ?.accounts?.firstOrNull { it.label == accountLabel }
            ?.operations
            ?: emptyList()

    private val _uiState = MutableStateFlow(
        OperationsUiState(
            bankName = bankName,
            accountLabel = accountLabel,
            operations = ops
        )
    )
    val uiState: StateFlow<OperationsUiState> = _uiState.asStateFlow()
}
