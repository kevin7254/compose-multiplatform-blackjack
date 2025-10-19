package com.kevin7254.blackjack.domain.bank.model

/**
 * Bet outcome - the result of placing a bet.
 * @param payout the number of chips won or lost.
 * @param newBankroll the new bankroll balance.
 */
data class BetOutcome(
    val payout: Chips,
    val newBankroll: Bankroll,
)