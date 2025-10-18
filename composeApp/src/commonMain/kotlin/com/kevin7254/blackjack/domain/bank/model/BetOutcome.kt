package com.kevin7254.blackjack.domain.bank.model

data class BetOutcome(
    val payout: Chips,
    val newBankroll: Bankroll,
)