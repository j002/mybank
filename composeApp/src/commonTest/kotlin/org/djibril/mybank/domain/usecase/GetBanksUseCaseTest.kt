package org.djibril.mybank.domain.usecase

import kotlinx.coroutines.test.runTest
import org.djibril.mybank.data.repository.BanksRepository
import org.djibril.mybank.domain.model.Bank
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetBanksUseCaseTest {

    @Test
    fun `Given repository returns banks When invoke Then returns same list`() = runTest {
        // Given
        val expected = listOf(
            Bank(name = "Crédit Agricole", isCa = true, accounts = emptyList()),
            Bank(name = "Boursorama", isCa = false, accounts = emptyList())
        )
        val repo = FakeBanksRepository(result = expected)
        val useCase = GetBanksUseCase(repository = repo)

        // When
        val result = useCase()

        // Then
        assertEquals(1, repo.callCount)
        assertEquals(expected, result)
    }

    @Test
    fun `Given repository throws When invoke Then rethrows`() = runTest {
        // Given
        val expectedError = IllegalStateException("boom")
        val repo = FakeBanksRepository(error = expectedError)
        val useCase = GetBanksUseCase(repository = repo)

        // When / Then
        val thrown = assertFailsWith<IllegalStateException> { useCase() }
        assertEquals("boom", thrown.message)
        assertEquals(1, repo.callCount)
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
