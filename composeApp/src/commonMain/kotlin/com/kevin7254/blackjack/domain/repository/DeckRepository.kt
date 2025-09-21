package com.kevin7254.blackjack.domain.repository

import com.kevin7254.blackjack.domain.model.Deck

interface DeckRepository {
    fun createStandardDeck(shuffle: Boolean): Deck
}