package org.djibril.mybank.ui.banks

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.djibril.mybank.domain.model.Bank
import org.djibril.mybank.domain.usecase.GetBanksUseCase

class BanksStore(
    private val getBanksUseCase: GetBanksUseCase,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
) {

    private val _uiState = MutableStateFlow(BanksUiState(loading = true))
    val uiState: StateFlow<BanksUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<BanksUiEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<BanksUiEvent> = _events.asSharedFlow()

    init {
        loadBanks()
    }

    fun loadBanks() {
        scope.launch {
            _uiState.update { it.copy(loading = true, errorMessage = null) }

            runCatching { getBanksUseCase() }
                .onSuccess { banks ->
                    val (ca, others) = banks.partition { it.isCa }
                    _uiState.update {
                        it.copy(
                            loading = false,
                            caBanks = ca,
                            otherBanks = others
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            loading = false,
                            caBanks = emptyList(),
                            otherBanks = emptyList(),
                            errorMessage = e.message ?: "Erreur lors du chargement"
                        )
                    }
                }
        }
    }

    fun toggleBank(bankName: String) {
        _uiState.update { state ->
            val expanded = state.expandedBankNames
            val updated =
                if (expanded.contains(bankName)) expanded - bankName else expanded + bankName
            state.copy(expandedBankNames = updated)
        }
    }

    fun onAccountClicked(bank: Bank, accountLabel: String) {
        _events.tryEmit(BanksUiEvent.OpenAccount(bank.name, accountLabel))
    }

    fun close() {
        scope.cancel()
    }
}
