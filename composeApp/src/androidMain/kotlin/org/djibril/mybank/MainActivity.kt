package org.djibril.mybank

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.ktor.client.engine.okhttp.OkHttp
import org.djibril.mybank.data.datasource.BanksDataSourceImpl
import org.djibril.mybank.data.mapper.BankMapper
import org.djibril.mybank.data.repository.BanksRepositoryImpl
import org.djibril.mybank.data.service.BanksServiceImpl
import org.djibril.mybank.domain.usecase.GetBanksUseCase
import org.djibril.mybank.network.createHttpClient


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val httpClient = createHttpClient(OkHttp.create())
        val service = BanksServiceImpl(client = httpClient)
        val dataSource = BanksDataSourceImpl(service)
        val repository = BanksRepositoryImpl(
            dataSource = dataSource,
            mapper = BankMapper()
        )
        val getBanksUseCase = GetBanksUseCase(repository)

        setContent {
            App(getBanksUseCase)
        }
    }
}
