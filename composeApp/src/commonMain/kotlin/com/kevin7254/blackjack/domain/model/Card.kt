package com.kevin7254.blackjack.domain.model

/**
 * A Card class that represents a playing card from a normal playing deck.
 * @param rank the rank of the card (Ace, 2, 3, 4, 5, 6, 7, 8, 9, 10, Jack, Queen, King)
 * @param suit the suit of the card (Clubs, Diamonds, Hearts, Spades)
 * @param isFaceUp whether the card is face up or face down
 */
data class Card(
    val rank: Rank,
    val suit: Suit,
    val isFaceUp: Boolean = true,
) : Comparable<Card> {
    /**
     * The name of the image file for this card.
     */
    val imageName: String
        get() = "_${rank.displayValue}${suit.shortName}"


    override fun compareTo(other: Card): Int {
        return (this.rank.value - other.rank.value).toInt()
    }

    override fun toString(): String {
        return "${rank.name} of ${suit.name}"
    }

    /**
     * Returns the integer value of this card.
     */
    fun toInt(): Int {
        return "${suit.ordinal}${rank.displayValue}".toInt()
    }
}