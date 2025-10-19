package com.kevin7254.blackjack.domain.bank.model

/**
 * The current state of the player's bet.
 * @param currentBet the current bet.
 * @param canPlaceBet whether the player can place a bet or not.
 */
data class BetState(
    val currentBet: Chips = Chips(0),
    val canPlaceBet: Boolean = true,
)