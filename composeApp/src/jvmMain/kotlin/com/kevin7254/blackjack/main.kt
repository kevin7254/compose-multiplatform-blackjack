package com.kevin7254.blackjack

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.kevin7254.blackjack.di.appModule
import com.kevin7254.blackjack.presentation.screens.BlackjackScreen
import com.kevin7254.blackjack.presentation.screens.MyApp
import org.koin.core.context.GlobalContext.startKoin

fun main() = application {
    startKoin {
        modules(appModule)
    }.koin

    Window(
        onCloseRequest = ::exitApplication,
        title = "BlackJack Game",
        state = rememberWindowState(width = 1200.dp, height = 1000.dp),
    ) {
        MyApp {
            BlackjackScreen()
        }
    }
}
