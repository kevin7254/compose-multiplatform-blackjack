package presentation.viewmodel

import domain.model.GameState

sealed interface BlackjackUiState {
    object Loading : BlackjackUiState
    data class Success(val gameState: GameState, val isAnimating: Boolean = false) : BlackjackUiState
    data class Error(val message: String) : BlackjackUiState
}