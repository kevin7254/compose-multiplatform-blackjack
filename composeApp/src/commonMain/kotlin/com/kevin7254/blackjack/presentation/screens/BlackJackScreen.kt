package com.kevin7254.blackjack.presentation.screens

import com.kevin7254.blackjack.presentation.viewmodel.BlackjackViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.koin.compose.koinInject
import com.kevin7254.blackjack.presentation.screens.components.GameTable
import com.kevin7254.blackjack.presentation.theme.Fonts
import com.kevin7254.blackjack.presentation.viewmodel.BlackjackUiState

@Composable
fun MyApp(content: @Composable () -> Unit) {
    MaterialTheme(
        typography = Fonts.typo(),
        content = content,
    )
}

@Composable
fun BlackjackScreen(
    viewModel: BlackjackViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2E7D32)),
        contentAlignment = Alignment.Center
    ) {
        when (val state = uiState) {
            is BlackjackUiState.Loading -> {
                CircularProgressIndicator(color = Color.White)
            }

            is BlackjackUiState.Error -> {
                Text(
                    text = "Error: ${state.message}",
                    color = Color.Red,
                    style = MaterialTheme.typography.displayMedium,
                )
            }

            is BlackjackUiState.Success -> {
                GameTable(
                    gameState = state.gameState,
                    strategyRecommendation = state.recommendation,
                    onPlayerHit = viewModel::onPlayerHit,
                    onPlayerStand = viewModel::onPlayerStand,
                    onNewGame = viewModel::onGameReset,
                )
            }
        }
    }
}
