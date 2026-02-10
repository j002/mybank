package org.djibril.mybank

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.djibril.mybank.domain.usecase.GetBanksUseCase
import org.djibril.mybank.ui.AppRoot
import org.djibril.mybank.ui.splash.SplashScreen
import org.djibril.mybank.ui.theme.MyBankTheme

@Composable
fun App(getBanksUseCase: GetBanksUseCase) {
    var showSplash by remember { mutableStateOf(true) }

    MyBankTheme {
        if (showSplash) {
            SplashScreen(
                onFinished = { showSplash = false }
            )
        } else {
            AppRoot(getBanksUseCase)
        }
    }
}

