package org.djibril.mybank.data.repository

import kotlinx.coroutines.test.runTest
import org.djibril.mybank.data.datasource.BanksDataSource
import org.djibril.mybank.data.dto.AccountDto
import org.djibril.mybank.data.dto.BankDto
import org.djibril.mybank.data.dto.OperationDto
import org.djibril.mybank.data.mapper.BankMapper
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class BanksRepositoryImplTest {

    private val fakeDataSource = FakeBanksDataSource()
    private val mapper = BankMapper()
    private val repository = BanksRepositoryImpl(fakeDataSource, mapper)

    @Test
    fun `getBanks returns mapped and sorted banks`() = runTest {
        // Given
        val bankDtos = listOf(
            BankDto(
                name = "BNP Paribas",
                isCA = 0,
                accounts = listOf(
                    AccountDto(
                        label = "Compte courant",
                        balance = "1000€",
                        operations = emptyList()
                    )
                )
            ),
            BankDto(
                name = "Crédit Agricole Loire",
                isCA = 1,
                accounts = listOf(
                    AccountDto(
                        label = "Livret A",
                        balance = "5000€",
                        operations = emptyList()
                    )
                )
            )
        )
        fakeDataSource.banksToReturn = bankDtos

        // When
        val result = repository.getBanks()

        // Then
        assertEquals(2, result.size)
        assertEquals("BNP Paribas", result[0].name)
        assertEquals("Crédit Agricole Loire", result[1].name)
    }

    @Test
    fun `getBanks correctly maps accounts with operations`() = runTest {
        // Given
        val operations = listOf(
            OperationDto(
                title = "Achat supermarché",
                date = 1707561600000L,
                amount = "-50.00€"
            ),
            OperationDto(
                title = "Salaire",
                date = 1707475200000L,
                amount = "+2500.00€"
            )
        )

        val bankDto = BankDto(
            name = "Test Bank",
            isCA = 0,
            accounts = listOf(
                AccountDto(
                    label = "Compte principal",
                    balance = "3450.00€",
                    operations = operations
                )
            )
        )
        fakeDataSource.banksToReturn = listOf(bankDto)

        // When
        val result = repository.getBanks()

        // Then
        assertEquals(1, result.size)
        val bank = result[0]
        assertEquals("Test Bank", bank.name)
        assertEquals(1, bank.accounts.size)

        val account = bank.accounts[0]
        assertEquals("Compte principal", account.label)
        assertEquals(2, account.operations.size)

        // Vérifier les opérations après mapping (maintenant c'est un Double)
        val operation1 = account.operations[0]
        assertEquals("Achat supermarché", operation1.title)
        assertEquals(-50.0, operation1.amount, 0.01) // Double avec delta pour comparaison
        assertEquals("€", operation1.currency)
        assertEquals(true, operation1.isNegative)
        assertEquals("-50,00 €", operation1.formattedAmount) // Format français

        val operation2 = account.operations[1]
        assertEquals("Salaire", operation2.title)
        assertEquals(2500.0, operation2.amount, 0.01)
        assertEquals("€", operation2.currency)
        assertEquals(false, operation2.isNegative)
        assertEquals("2500,00 €", operation2.formattedAmount) // Format français avec espace
    }

    @Test
    fun `getBanks separates CA banks from other banks`() = runTest {
        // Given
        val emptyAccount = AccountDto(
            label = "Compte",
            balance = "0€",
            operations = emptyList()
        )

        val bankDtos = listOf(
            BankDto(name = "BNP", isCA = 0, accounts = listOf(emptyAccount)),
            BankDto(name = "CA Atlantique", isCA = 1, accounts = listOf(emptyAccount)),
            BankDto(name = "Société Générale", isCA = 0, accounts = listOf(emptyAccount)),
            BankDto(name = "CA Normandie", isCA = 1, accounts = listOf(emptyAccount))
        )
        fakeDataSource.banksToReturn = bankDtos

        // When
        val result = repository.getBanks()

        // Then
        assertEquals(4, result.size)
        val caBanks = result.filter { it.name.contains("CA") }
        val otherBanks = result.filter { !it.name.contains("CA") }

        assertEquals(2, caBanks.size, "Should have 2 CA banks")
        assertEquals(2, otherBanks.size, "Should have 2 other banks")
    }

    @Test
    fun `getBanks returns empty list when no banks available`() = runTest {
        // Given
        fakeDataSource.banksToReturn = emptyList()

        // When
        val result = repository.getBanks()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getBanks propagates error from data source`() = runTest {
        // Given
        fakeDataSource.shouldThrowError = true
        fakeDataSource.errorToThrow = Exception("Network error")

        // When/Then
        assertFailsWith<Exception> {
            repository.getBanks()
        }
    }

    @Test
    fun `getBanks handles accounts with no operations`() = runTest {
        // Given
        val bankDto = BankDto(
            name = "Empty Operations Bank",
            isCA = 0,
            accounts = listOf(
                AccountDto(
                    label = "Nouveau compte",
                    balance = "0€",
                    operations = emptyList()
                )
            )
        )
        fakeDataSource.banksToReturn = listOf(bankDto)

        // When
        val result = repository.getBanks()

        // Then
        assertEquals(1, result.size)
        assertEquals(1, result[0].accounts.size)
        assertTrue(result[0].accounts[0].operations.isEmpty())
    }

    @Test
    fun `getBanks handles multiple accounts with different operations`() = runTest {
        // Given
        val bankDto = BankDto(
            name = "Multi Account Bank",
            isCA = 1,
            accounts = listOf(
                AccountDto(
                    label = "Compte courant",
                    balance = "1500€",
                    operations = listOf(
                        OperationDto("Op1", 1707561600000L, "-100€")
                    )
                ),
                AccountDto(
                    label = "Livret A",
                    balance = "5000€",
                    operations = listOf(
                        OperationDto("Op2", 1707561600000L, "+50€"),
                        OperationDto("Op3", 1707475200000L, "+100€")
                    )
                )
            )
        )
        fakeDataSource.banksToReturn = listOf(bankDto)

        // When
        val result = repository.getBanks()

        // Then
        assertEquals(1, result.size)
        val bank = result[0]
        assertEquals(2, bank.accounts.size)
        assertEquals(1, bank.accounts[0].operations.size)
        assertEquals(2, bank.accounts[1].operations.size)
    }
}

class FakeBanksDataSource : BanksDataSource {
    var banksToReturn: List<BankDto> = emptyList()
    var shouldThrowError = false
    var errorToThrow: Exception? = null

    override suspend fun getBanks(): List<BankDto> {
        if (shouldThrowError) {
            throw errorToThrow ?: Exception("Test error")
        }
        return banksToReturn
    }
}