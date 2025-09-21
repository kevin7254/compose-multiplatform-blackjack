package com.kevin7254.blackjack

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.kevin7254.blackjack.di.appModule
import com.kevin7254.blackjack.presentation.screens.BlackjackScreen
import com.kevin7254.blackjack.presentation.screens.MyApp
import org.koin.core.context.startKoin

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport {
        startKoin {
            modules(appModule)
        }.koin

        MyApp {
            BlackjackScreen()
        }
    }
}