package org.djibril.mybank.data.datasource

import kotlinx.coroutines.test.runTest
import org.djibril.mybank.data.dto.BankDto
import org.djibril.mybank.data.service.BanksService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BanksDataSourceImplTest {

    @Test
    fun `getBanks returns banks from service`() = runTest {
        // GIVEN
        val expected = listOf(
            BankDto(
                name = "Credit Agricole",
                isCA = 1,
                accounts = emptyList()
            ),
            BankDto(
                name = "BNP",
                isCA = 0,
                accounts = emptyList()
            )
        )

        val service = FakeBanksService(result = expected)
        val dataSource = BanksDataSourceImpl(service)

        // WHEN
        val result = dataSource.getBanks()

        // THEN
        assertEquals(expected, result)
        assertEquals(1, service.callCount)
    }

    @Test
    fun `getBanks propagates service exception`() = runTest {
        // GIVEN
        val service = FakeBanksService(
            error = IllegalStateException("network error")
        )
        val dataSource = BanksDataSourceImpl(service)

        // WHEN / THEN
        val exception = assertFailsWith<IllegalStateException> {
            dataSource.getBanks()
        }

        assertEquals("network error", exception.message)
        assertEquals(1, service.callCount)
    }

    // ---- Fake service (KMP-safe) ----
    private class FakeBanksService(
        private val result: List<BankDto> = emptyList(),
        private val error: Throwable? = null
    ) : BanksService {

        var callCount: Int = 0
            private set

        override suspend fun fetchBanks(): List<BankDto> {
            callCount++
            error?.let { throw it }
            return result
        }
    }
}