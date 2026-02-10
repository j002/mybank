package org.djibril.mybank.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.djibril.mybank.domain.model.Bank
import org.djibril.mybank.domain.usecase.GetBanksUseCase
import org.djibril.mybank.ui.banks.BanksScreen
import org.djibril.mybank.ui.banks.BanksStore
import org.djibril.mybank.ui.operations.OperationsScreen
import org.djibril.mybank.ui.operations.OperationsStore

@Composable
fun AppRoot(
    getBanksUseCase: GetBanksUseCase
) {
    MaterialTheme {
        var screen by remember { mutableStateOf<Screen>(Screen.Banks) }

        val banksStore = remember { BanksStore(getBanksUseCase) }
        DisposableEffect(Unit) {
            onDispose { banksStore.close() }
        }

        val banksState by banksStore.uiState.collectAsState()
        val cachedBanks: List<Bank> = banksState.caBanks + banksState.otherBanks

        when (val s = screen) {
            Screen.Banks -> {
                BanksScreen(
                    store = banksStore,
                    onOpenAccount = { bankName, accountLabel ->
                        screen = Screen.Operations(bankName, accountLabel)
                    }
                )
            }

            is Screen.Operations -> {
                val opsStore = remember(s.bankName, s.accountLabel, cachedBanks) {
                    OperationsStore(
                        bankName = s.bankName,
                        accountLabel = s.accountLabel,
                        banks = cachedBanks
                    )
                }
                OperationsScreen(
                    store = opsStore,
                    onBack = { screen = Screen.Banks }
                )
            }
        }
    }
}
