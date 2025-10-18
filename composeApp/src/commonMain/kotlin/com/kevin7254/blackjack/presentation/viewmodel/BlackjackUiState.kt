package com.kevin7254.blackjack.presentation.viewmodel

import com.kevin7254.blackjack.domain.bank.model.Bankroll
import com.kevin7254.blackjack.domain.bank.model.BetState
import com.kevin7254.blackjack.domain.model.GameState
import com.kevin7254.blackjack.domain.model.RoundPhase
import com.kevin7254.blackjack.domain.usecase.StrategyRecommendation

sealed interface BlackjackUiState {
    object Loading : BlackjackUiState

    data class Success(
        val gameState: GameState,
        val recommendation: StrategyRecommendation,
        val isAnimating: Boolean = false,
        val roundPhase: RoundPhase,
        val bankroll: Bankroll,
        val betState: BetState,
    ) : BlackjackUiState

    data class Error(val message: String) : BlackjackUiState
}