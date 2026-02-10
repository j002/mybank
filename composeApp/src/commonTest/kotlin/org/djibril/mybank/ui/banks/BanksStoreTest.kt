package org.djibril.mybank.ui.banks

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.yield
import org.djibril.mybank.data.repository.BanksRepository
import org.djibril.mybank.domain.model.Bank
import org.djibril.mybank.domain.usecase.GetBanksUseCase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class BanksStoreTest {

    @Test
    fun `Given usecase returns banks When store init Then state contains CA and other banks`() =
        runBlocking {
            // Given
            val banks = listOf(
                Bank(name = "Crédit Agricole", isCa = true, accounts = emptyList()),
                Bank(name = "Boursorama", isCa = false, accounts = emptyList()),
                Bank(name = "CA Languedoc", isCa = true, accounts = emptyList())
            )

            val repo = FakeBanksRepository(result = banks)
            val useCase = GetBanksUseCase(repository = repo)

            val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
            val store = BanksStore(getBanksUseCase = useCase, scope = scope)

            try {
                // When
                val state = awaitLoaded(store)

                // Then
                assertFalse(state.loading)
                assertNull(state.errorMessage)

                assertEquals(2, state.caBanks.size)
                assertTrue(state.caBanks.all { it.isCa })

                assertEquals(1, state.otherBanks.size)
                assertTrue(state.otherBanks.all { !it.isCa })

                assertEquals(1, repo.callCount)
            } finally {
                store.close()
            }
        }

    @Test
    fun `Given usecase throws When loadBanks Then state contains error and lists cleared`() =
        runBlocking {
            // Given
            val repo = FakeBanksRepository(error = IllegalStateException("network down"))
            val useCase = GetBanksUseCase(repository = repo)

            val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
            val store = BanksStore(getBanksUseCase = useCase, scope = scope)

            try {
                // When
                val state = awaitLoaded(store)

                // Then
                assertFalse(state.loading)
                assertTrue(state.caBanks.isEmpty())
                assertTrue(state.otherBanks.isEmpty())
                assertNotNull(state.errorMessage)
                assertEquals("network down", state.errorMessage)
                assertEquals(1, repo.callCount)
            } finally {
                store.close()
            }
        }

    @Test
    fun `Given a collapsed bank When toggleBank Then it becomes expanded and toggles back`() =
        runBlocking {
            // Given
            val repo = FakeBanksRepository(result = emptyList())
            val useCase = GetBanksUseCase(repository = repo)

            val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
            val store = BanksStore(getBanksUseCase = useCase, scope = scope)

            try {
                // Attendre la fin du load initial (même si vide)
                awaitLoaded(store)

                val bankName = "Crédit Agricole"

                // When 1
                store.toggleBank(bankName)

                // Then 1
                val afterExpand = store.uiState.value
                assertTrue(afterExpand.expandedBankNames.contains(bankName))

                // When 2
                store.toggleBank(bankName)

                // Then 2
                val afterCollapse = store.uiState.value
                assertFalse(afterCollapse.expandedBankNames.contains(bankName))
            } finally {
                store.close()
            }
        }

    @Test
    fun `When onAccountClicked Then emits OpenAccount event`() = runBlocking {
        // Given
        val repo = FakeBanksRepository(result = emptyList())
        val useCase = GetBanksUseCase(repository = repo)

        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        val store = BanksStore(getBanksUseCase = useCase, scope = scope)

        try {
            awaitLoaded(store)

            val bank = Bank(name = "Crédit Agricole", isCa = true, accounts = emptyList())
            val accountLabel = "Compte principal"

            val eventDeferred = async {
                withTimeout(1_000) { store.events.first() }
            }

            yield()

            // When
            store.onAccountClicked(bank, accountLabel)

            // Then
            when (val event = eventDeferred.await()) {
                is BanksUiEvent.OpenAccount -> {
                    assertEquals("Crédit Agricole", event.bankName)
                    assertEquals("Compte principal", event.accountLabel)
                }
            }
        } finally {
            store.close()
        }
    }

    // -----------------------------
    // Helpers
    // -----------------------------

    private suspend fun awaitLoaded(store: BanksStore): BanksUiState =
        withTimeout(2_000) {
            store.uiState
                .filter { !it.loading }
                .first()
        }

    private class FakeBanksRepository(
        private val result: List<Bank> = emptyList(),
        private val error: Throwable? = null
    ) : BanksRepository {

        var callCount: Int = 0
            private set

        override suspend fun getBanks(): List<Bank> {
            callCount++
            error?.let { throw it }
            return result
        }
    }
}
