package com.kevin7254.blackjack.domain.model

import androidx.compose.runtime.Immutable

/**
 * Represents the current state of a blackjack game.
 * This is a domain model that encapsulates all the information needed to represent a game state.
 */
@Immutable
data class GameState(
    val deck: Deck,
    val playerCards: Hand,
    val dealerCards: Hand,
    val gameResult: GameResult,
)