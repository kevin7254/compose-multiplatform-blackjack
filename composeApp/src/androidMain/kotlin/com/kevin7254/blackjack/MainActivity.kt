package com.kevin7254.blackjack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.kevin7254.blackjack.di.appModule
import com.kevin7254.blackjack.presentation.screens.BlackjackScreen
import com.kevin7254.blackjack.presentation.screens.MyApp
import org.koin.core.context.GlobalContext.startKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        startKoin {
            modules(appModule)
        }.koin

        setContent {
            MyApp {
                BlackjackScreen()
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    MyApp {
        BlackjackScreen()
    }
}