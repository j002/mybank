package org.djibril.mybank.data.datasource

import org.djibril.mybank.data.dto.BankDto

interface BanksDataSource {
    suspend fun getBanks(): List<BankDto>
}