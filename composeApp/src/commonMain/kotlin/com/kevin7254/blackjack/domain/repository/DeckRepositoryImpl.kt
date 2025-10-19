package com.kevin7254.blackjack.domain.repository

import com.kevin7254.blackjack.domain.model.Deck

class DeckRepositoryImpl : DeckRepository {
    override fun createStandardDeck(shuffle: Boolean) = Deck.createStandardDeck(shuffle)
}