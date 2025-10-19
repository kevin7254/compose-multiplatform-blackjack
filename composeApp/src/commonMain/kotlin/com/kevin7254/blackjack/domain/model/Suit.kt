package com.kevin7254.blackjack.domain.model

/**
 * Represents the suit of a card.
 * Can be either a club, diamond, heart, or spade.
 * @param shortName the short name of the suit.
 */
enum class Suit(val shortName: String) {
    CLUBS("C"),
    DIAMONDS("D"),
    HEARTS("H"),
    SPADES("S")
}
