package com.kevin7254.blackjack.presentation.viewmodel

import com.kevin7254.blackjack.domain.model.GameState
import com.kevin7254.blackjack.domain.usecase.StrategyRecommendation

sealed interface BlackjackUiState {
    object Loading : BlackjackUiState

    data class Success(
        val gameState: GameState,
        val recommendation: StrategyRecommendation,
        val isAnimating: Boolean = false,
    ) : BlackjackUiState

    data class Error(val message: String) : BlackjackUiState
}