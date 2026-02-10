package org.djibril.mybank.domain.usecase

import org.djibril.mybank.data.repository.BanksRepository

class GetBanksUseCase(
    private val repository: BanksRepository
) {
    suspend operator fun invoke() = repository.getBanks()

}
