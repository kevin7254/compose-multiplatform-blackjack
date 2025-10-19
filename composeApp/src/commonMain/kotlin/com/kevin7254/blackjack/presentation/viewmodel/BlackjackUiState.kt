package com.kevin7254.blackjack.presentation.viewmodel

import com.kevin7254.blackjack.domain.bank.model.Bankroll
import com.kevin7254.blackjack.domain.bank.model.BetState
import com.kevin7254.blackjack.domain.model.GameState
import com.kevin7254.blackjack.domain.model.RoundPhase
import com.kevin7254.blackjack.domain.usecase.StrategyRecommendation

/**
 * The UI state of the Blackjack game.
 * @see GameState
 * @see StrategyRecommendation
 * @see RoundPhase
 * @see BetState
 * @see Bankroll
 */
sealed interface BlackjackUiState {
    /**
     * The game is loading.
     */
    object Loading : BlackjackUiState

    /**
     * The game has loaded successfully.
     * @see GameState
     * @see StrategyRecommendation
     * @see RoundPhase
     * @see BetState
     * @see Bankroll
     */
    data class Success(
        val gameState: GameState,
        val recommendation: StrategyRecommendation,
        val isAnimating: Boolean = false,
        val roundPhase: RoundPhase,
        val bankroll: Bankroll,
        val betState: BetState,
    ) : BlackjackUiState

    /**
     * An error occurred during game loading.
     * @property message The error message.
     */
    data class Error(val message: String) : BlackjackUiState
}