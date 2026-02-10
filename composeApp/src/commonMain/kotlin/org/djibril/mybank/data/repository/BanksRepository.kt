package org.djibril.mybank.data.repository

import org.djibril.mybank.domain.model.Bank

interface BanksRepository {
    suspend fun getBanks(): List<Bank>
}