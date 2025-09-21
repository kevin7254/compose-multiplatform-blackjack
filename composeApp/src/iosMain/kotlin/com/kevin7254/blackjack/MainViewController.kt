package com.kevin7254.blackjack

import androidx.compose.ui.window.ComposeUIViewController
import com.kevin7254.blackjack.di.appModule
import com.kevin7254.blackjack.presentation.screens.BlackjackScreen
import com.kevin7254.blackjack.presentation.screens.MyApp
import org.koin.core.context.startKoin

fun MainViewController() = ComposeUIViewController {
    startKoin {
        modules(appModule)
    }.koin

    MyApp {
        BlackjackScreen()
    }
}