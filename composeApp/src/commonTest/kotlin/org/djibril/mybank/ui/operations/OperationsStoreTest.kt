package org.djibril.mybank.ui.operations

import org.djibril.mybank.domain.model.Account
import org.djibril.mybank.domain.model.Bank
import org.djibril.mybank.domain.model.Operation
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OperationsStoreTest {

    @Test
    fun `Given matching bank and account When store init Then state contains operations`() {
        // Given
        val operations = listOf(
            Operation(
                title = "Achat supermarché",
                epochSeconds = 1707561600L,
                amount = -50.0,
                currency = "€"
            ),
            Operation(
                title = "Salaire",
                epochSeconds = 1707475200L,
                amount = 2500.0,
                currency = "€"
            )
        )

        val account = Account(
            label = "Compte courant",
            balance = "2450€",
            operations = operations
        )

        val bank = Bank(
            name = "Crédit Agricole",
            isCa = true,
            accounts = listOf(account)
        )

        val banks = listOf(bank)

        // When
        val store = OperationsStore(
            bankName = "Crédit Agricole",
            accountLabel = "Compte courant",
            banks = banks
        )

        // Then
        val state = store.uiState.value
        assertEquals("Crédit Agricole", state.bankName)
        assertEquals("Compte courant", state.accountLabel)
        assertEquals(2, state.operations.size)
        assertEquals("Achat supermarché", state.operations[0].title)
        assertEquals("Salaire", state.operations[1].title)
    }

    @Test
    fun `Given bank not found When store init Then state has empty operations`() {
        // Given
        val bank = Bank(
            name = "BNP Paribas",
            isCa = false,
            accounts = listOf(
                Account(
                    label = "Compte",
                    balance = "1000€",
                    operations = listOf(
                        Operation("Op", 1707561600L, -10.0, "€")
                    )
                )
            )
        )

        val banks = listOf(bank)

        // When
        val store = OperationsStore(
            bankName = "Crédit Agricole", // Banque inexistante
            accountLabel = "Compte",
            banks = banks
        )

        // Then
        val state = store.uiState.value
        assertEquals("Crédit Agricole", state.bankName)
        assertEquals("Compte", state.accountLabel)
        assertTrue(state.operations.isEmpty())
    }

    @Test
    fun `Given account not found When store init Then state has empty operations`() {
        // Given
        val bank = Bank(
            name = "Crédit Agricole",
            isCa = true,
            accounts = listOf(
                Account(
                    label = "Livret A",
                    balance = "5000€",
                    operations = listOf(
                        Operation("Intérêts", 1707561600L, 10.0, "€")
                    )
                )
            )
        )

        val banks = listOf(bank)

        // When
        val store = OperationsStore(
            bankName = "Crédit Agricole",
            accountLabel = "Compte courant", // Compte inexistant
            banks = banks
        )

        // Then
        val state = store.uiState.value
        assertEquals("Crédit Agricole", state.bankName)
        assertEquals("Compte courant", state.accountLabel)
        assertTrue(state.operations.isEmpty())
    }

    @Test
    fun `Given account with no operations When store init Then state has empty operations`() {
        // Given
        val bank = Bank(
            name = "Société Générale",
            isCa = false,
            accounts = listOf(
                Account(
                    label = "Nouveau compte",
                    balance = "0€",
                    operations = emptyList()
                )
            )
        )

        val banks = listOf(bank)

        // When
        val store = OperationsStore(
            bankName = "Société Générale",
            accountLabel = "Nouveau compte",
            banks = banks
        )

        // Then
        val state = store.uiState.value
        assertEquals("Société Générale", state.bankName)
        assertEquals("Nouveau compte", state.accountLabel)
        assertTrue(state.operations.isEmpty())
    }

    @Test
    fun `Given empty banks list When store init Then state has empty operations`() {
        // Given
        val banks = emptyList<Bank>()

        // When
        val store = OperationsStore(
            bankName = "Any Bank",
            accountLabel = "Any Account",
            banks = banks
        )

        // Then
        val state = store.uiState.value
        assertEquals("Any Bank", state.bankName)
        assertEquals("Any Account", state.accountLabel)
        assertTrue(state.operations.isEmpty())
    }

    @Test
    fun `Given multiple accounts When store init Then only matching account operations are loaded`() {
        // Given
        val operations1 = listOf(
            Operation("Op1", 1707561600L, -10.0, "€")
        )
        val operations2 = listOf(
            Operation("Op2", 1707475200L, -20.0, "€"),
            Operation("Op3", 1707388800L, -30.0, "€")
        )

        val bank = Bank(
            name = "BNP",
            isCa = false,
            accounts = listOf(
                Account("Compte A", "100€", operations1),
                Account("Compte B", "200€", operations2),
                Account("Compte C", "300€", emptyList())
            )
        )

        val banks = listOf(bank)

        // When
        val store = OperationsStore(
            bankName = "BNP",
            accountLabel = "Compte B",
            banks = banks
        )

        // Then
        val state = store.uiState.value
        assertEquals(2, state.operations.size)
        assertEquals("Op2", state.operations[0].title)
        assertEquals("Op3", state.operations[1].title)
    }

    @Test
    fun `Given multiple banks When store init Then only matching bank is searched`() {
        // Given
        val banks = listOf(
            Bank(
                name = "Banque A",
                isCa = false,
                accounts = listOf(
                    Account(
                        "Compte",
                        "100€",
                        listOf(Operation("Wrong", 1707561600L, -10.0, "€"))
                    )
                )
            ),
            Bank(
                name = "Banque B",
                isCa = true,
                accounts = listOf(
                    Account(
                        "Compte",
                        "200€",
                        listOf(Operation("Correct", 1707475200L, -20.0, "€"))
                    )
                )
            )
        )

        // When
        val store = OperationsStore(
            bankName = "Banque B",
            accountLabel = "Compte",
            banks = banks
        )

        // Then
        val state = store.uiState.value
        assertEquals(1, state.operations.size)
        assertEquals("Correct", state.operations[0].title)
    }

    @Test
    fun `State flow emits initial state immediately`() {
        // Given
        val operations = listOf(
            Operation("Test", 1707561600L, -100.0, "€")
        )

        val bank = Bank(
            name = "Test Bank",
            isCa = false,
            accounts = listOf(
                Account("Test Account", "900€", operations)
            )
        )

        // When
        val store = OperationsStore(
            bankName = "Test Bank",
            accountLabel = "Test Account",
            banks = listOf(bank)
        )

        // Then
        val state = store.uiState.value
        assertEquals("Test Bank", state.bankName)
        assertEquals("Test Account", state.accountLabel)
        assertEquals(1, state.operations.size)
    }
}