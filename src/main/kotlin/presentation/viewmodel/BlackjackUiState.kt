package presentation.viewmodel

import domain.model.GameState
import domain.usecase.StrategyRecommendation

sealed interface BlackjackUiState {
    object Loading : BlackjackUiState

    data class Success(
        val gameState: GameState,
        val recommendation: StrategyRecommendation,
        val isAnimating: Boolean = false,
    ) : BlackjackUiState

    data class Error(val message: String) : BlackjackUiState
}