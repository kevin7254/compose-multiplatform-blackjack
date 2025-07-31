package domain.repository

import domain.model.Deck

class DeckRepositoryImpl : DeckRepository {
    override fun createStandardDeck(shuffle: Boolean): Deck {
        val deck = Deck.createStandardDeck(shuffle)
        return deck
    }
}