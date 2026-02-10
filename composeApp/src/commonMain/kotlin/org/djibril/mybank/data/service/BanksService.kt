package org.djibril.mybank.data.service


import org.djibril.mybank.data.dto.BankDto

interface BanksService {
    suspend fun fetchBanks(): List<BankDto>
}


