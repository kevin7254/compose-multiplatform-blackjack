package com.kevin7254.blackjack.domain.model

import androidx.compose.runtime.Immutable

/**
 * Represents a deck of [Card]s.
 * This is a domain model that encapsulates all the information needed to represent a deck of cards.
 * @param cards the list of cards in the deck
 * @param numberOfDecks the number of decks in the deck. Normally 1.
 */
@Immutable
data class Deck(
    val cards: List<Card> = emptyList(),
    val numberOfDecks: Int = 1,
) : Iterable<Card> {

    override fun iterator(): Iterator<Card> = cards.iterator()

    /**
     * Returns the number of cards in the deck.
     */
    fun size(): Int = cards.size

    /**
     * Returns true if the deck contains the specified [card].
     */
    fun contains(card: Card): Boolean = cards.contains(card)

    companion object {
        /**
         * Creates a standard deck of 52 cards.
         */
        fun createStandardDeck(shuffle: Boolean = false): Deck {
            val cards = Suit.entries.flatMap { suit ->
                Rank.entries.map { rank ->
                    Card(rank, suit)
                }
            }.let { if (shuffle) it.shuffled() else it }

            return Deck(cards)
        }
    }
}
