package com.kevin7254.blackjack.domain.bank.model

data class BetState(
    val currentBet: Chips = Chips(0),
    val canPlaceBet: Boolean = true,
)