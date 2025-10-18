package com.kevin7254.blackjack.domain.bank.model

import kotlin.jvm.JvmInline

@JvmInline
value class Bankroll(val balance: Chips) {
    fun copy(balance: Chips) = Bankroll(balance)
    operator fun plus(other: Bankroll) = Bankroll(balance + other.balance)
    operator fun minus(other: Bankroll) = Bankroll(balance - other.balance)
    operator fun times(other: Double) = Bankroll(balance * other)
}
