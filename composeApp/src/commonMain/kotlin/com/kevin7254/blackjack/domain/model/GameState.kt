package com.kevin7254.blackjack.domain.model

import androidx.compose.runtime.Immutable

/**
 * Represents the current state of a blackjack game.
 * This is a domain model that encapsulates all the information needed to represent a game state.
 * @param deck the deck of cards currently in the game.
 * @param playerCards the cards currently held by the player.
 * @param dealerCards the cards currently held by the dealer.
 * @param status whether the round is in progress or finished with an outcome.
 */
@Immutable
data class GameState(
    val deck: Deck,
    val playerCards: Hand,
    val dealerCards: Hand,
    val status: RoundStatus,
) {
    companion object {
        /**
         * Creates an empty game state.
         */
        fun empty(): GameState {
            return GameState(
                Deck(),
                Hand(),
                Hand(),
                RoundStatus.InProgress,
            )
        }
    }
}