package domain.repository

import domain.model.Deck

interface DeckRepository {
    fun createStandardDeck(shuffle: Boolean): Deck
}