package org.djibril.mybank.data.mapper

import org.djibril.mybank.data.dto.AccountDto
import org.djibril.mybank.data.dto.BankDto
import org.djibril.mybank.data.dto.OperationDto
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class BankMapperTest {

    private val mapper = BankMapper()

    @Test
    fun map_shouldMapBasicFields_andIsCaTrue_whenIsCAis1() {
        // Given
        val dto = BankDto(
            name = "Crédit Agricole",
            isCA = 1,
            accounts = emptyList()
        )

        // When
        val result = mapper.map(dto)

        // Then
        assertEquals("Crédit Agricole", result.name)
        assertTrue(result.isCa)
        assertEquals(0, result.accounts.size)
    }

    @Test
    fun map_shouldMapIsCaFalse_whenIsCAis0() {
        // Given
        val dto = BankDto(
            name = "Autre Banque",
            isCA = 0,
            accounts = emptyList()
        )

        // When
        val result = mapper.map(dto)

        // Then
        assertEquals("Autre Banque", result.name)
        assertEquals(false, result.isCa)
    }

    @Test
    fun map_shouldMapAccounts_andOperations_andCurrencyShouldBeEuro() {
        // Given
        val op1 = OperationDto(title = "CB AMAZON", date = 1700000000L, amount = "-45,99")
        val op2 = OperationDto(title = "Salaire", date = 1700003600L, amount = "1200,00")

        val account = AccountDto(
            label = "Compte courant",
            balance = "1 234,56 €",
            operations = listOf(op1, op2)
        )

        val dto = BankDto(
            name = "Crédit Agricole",
            isCA = 1,
            accounts = listOf(account)
        )

        // When
        val result = mapper.map(dto)

        // Then
        assertEquals(1, result.accounts.size)
        val mappedAccount = result.accounts.first()
        assertEquals("Compte courant", mappedAccount.label)
        assertEquals("1 234,56 €", mappedAccount.balance)

        // operations mapped
        assertEquals(2, mappedAccount.operations.size)

        // On cherche les 2 ops par titre (car tri potentiel via OperationSorter)
        val mappedOp1 = mappedAccount.operations.firstOrNull { it.title == "CB AMAZON" }
        val mappedOp2 = mappedAccount.operations.firstOrNull { it.title == "Salaire" }

        assertNotNull(mappedOp1)
        assertNotNull(mappedOp2)

        assertEquals("€", mappedOp1.currency)
        assertEquals("€", mappedOp2.currency)

        // Amount parsing
        assertEquals(-45.99, mappedOp1.amount, 0.0001)
        assertEquals(1200.00, mappedOp2.amount, 0.0001)

        // Date normalization (on ne teste pas une valeur exacte, juste une propriété stable)
        assertTrue(mappedOp1.epochSeconds > 0)
        assertTrue(mappedOp2.epochSeconds > 0)
    }

    @Test
    fun map_shouldKeepOperationsCount_evenIfSorterChangesOrder() {
        // Given: dates volontairement inversées pour que OperationSorter puisse trier
        val opOld = OperationDto(title = "Old", date = 100L, amount = "10,00")
        val opNew = OperationDto(title = "New", date = 200L, amount = "20,00")

        val account = AccountDto(
            label = "Compte test",
            balance = "0,00 €",
            operations = listOf(opNew, opOld)
        )
        val dto = BankDto(name = "B", isCA = 0, accounts = listOf(account))

        // When
        val result = mapper.map(dto)

        // Then
        val ops = result.accounts.first().operations
        assertEquals(2, ops.size)
        assertNotNull(ops.firstOrNull { it.title == "Old" })
        assertNotNull(ops.firstOrNull { it.title == "New" })
    }
}
