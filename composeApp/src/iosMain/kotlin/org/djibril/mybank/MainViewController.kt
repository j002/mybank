package org.djibril.mybank

import androidx.compose.ui.window.ComposeUIViewController
import io.ktor.client.engine.darwin.Darwin
import org.djibril.mybank.data.datasource.BanksDataSourceImpl
import org.djibril.mybank.data.mapper.BankMapper
import org.djibril.mybank.data.repository.BanksRepositoryImpl
import org.djibril.mybank.data.service.BanksServiceImpl
import org.djibril.mybank.domain.usecase.GetBanksUseCase
import org.djibril.mybank.network.createHttpClient

fun MainViewController() = ComposeUIViewController {
    val httpClient = createHttpClient(Darwin.create())
    val service = BanksServiceImpl(client = httpClient)
    val dataSource = BanksDataSourceImpl(service)
    val repository = BanksRepositoryImpl(
        dataSource = dataSource,
        mapper = BankMapper()
    )
    val getBanksUseCase = GetBanksUseCase(repository)
    App(getBanksUseCase)
}
