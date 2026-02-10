package org.djibril.mybank.data.service

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import org.djibril.mybank.data.dto.BankDto

class BanksServiceImpl(
    private val client: HttpClient
) : BanksService {

    override suspend fun fetchBanks(): List<BankDto> {
        return client
            .get(BanksEndpoints.BANKS)
            .body()
    }
}
