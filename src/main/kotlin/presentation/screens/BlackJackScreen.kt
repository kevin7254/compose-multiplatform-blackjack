package presentation.screens

import presentation.viewmodel.BlackjackViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.koin.compose.koinInject
import presentation.screens.components.GameTable
import presentation.theme.Fonts
import presentation.viewmodel.BlackjackUiState

@Composable
fun MyApp(content: @Composable () -> Unit) {
    MaterialTheme(
        typography = Fonts.customTypography(),
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
                    style = MaterialTheme.typography.h5,
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
