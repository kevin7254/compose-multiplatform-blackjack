package com.kevin7254.blackjack.domain.bank.model

import kotlin.jvm.JvmInline

/**
 * Chips class - the "money" of the game.
 * Cannot be negative.
 * @param amount the current number of chips.
 */
@JvmInline
value class Chips(val amount: Int) {
    init {
        require(amount >= 0) { "Chips must be non-negative" }
    }

    operator fun plus(other: Chips) = Chips(amount + other.amount)
    operator fun minus(other: Chips) = Chips((amount - other.amount).coerceAtLeast(0))
    operator fun times(other: Double) = Chips((amount * other).toInt())
}
