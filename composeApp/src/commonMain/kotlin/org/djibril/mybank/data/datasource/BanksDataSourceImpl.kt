package org.djibril.mybank.data.datasource

import org.djibril.mybank.data.dto.BankDto
import org.djibril.mybank.data.service.BanksService

class BanksDataSourceImpl(
    private val service: BanksService
) : BanksDataSource {

    override suspend fun getBanks(): List<BankDto> {
        return service.fetchBanks()
    }
}
