package com.kevin7254.blackjack.domain.repository

import com.kevin7254.blackjack.domain.model.Deck

/**
 * Deck repository responsible for creating a standard deck.
 */
interface DeckRepository {
    /**
     * Create a standard deck.
     * @param shuffle Whether to shuffle the cards before returning.
     * @return The created deck.
     */
    fun createStandardDeck(shuffle: Boolean): Deck
}