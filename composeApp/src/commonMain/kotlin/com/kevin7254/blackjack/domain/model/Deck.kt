package com.kevin7254.blackjack.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class Deck(
    val cards: List<Card> = emptyList(),
    val numberOfDecks: Int = 1,
) : Iterable<Card> {

    override fun iterator(): Iterator<Card> = cards.iterator()

    fun size(): Int = cards.size

    fun contains(card: Card): Boolean = cards.contains(card)

    companion object {
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
