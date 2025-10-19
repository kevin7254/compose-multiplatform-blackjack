package com.kevin7254.blackjack.domain.model

data class Card(
    val rank: Rank,
    val suit: Suit,
    val isFaceUp: Boolean = true,
) : Comparable<Card> {
    val imageName: String
        get() = "_${rank.displayValue}${suit.shortName}"


    override fun compareTo(other: Card): Int {
        return (this.rank.value - other.rank.value).toInt()
    }

    override fun toString(): String {
        return "${rank.name} of ${suit.name}"
    }
    fun toInt(): Int {
        return "${suit.ordinal}${rank.displayValue}".toInt()
    }
}