package org.djibril.mybank.data.repository

import org.djibril.mybank.data.datasource.BanksDataSource
import org.djibril.mybank.data.mapper.BankMapper
import org.djibril.mybank.domain.model.Bank
import org.djibril.mybank.utils.BankSorter

class BanksRepositoryImpl(
    private val dataSource: BanksDataSource,
    private val mapper: BankMapper
) : BanksRepository {

    override suspend fun getBanks(): List<Bank> {
        val bankDtos = dataSource.getBanks()
        val banks = bankDtos.map { mapper.map(it) }
        return BankSorter.sort(banks)
    }
}
