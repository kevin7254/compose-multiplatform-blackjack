package com.kevin7254.blackjack.domain.usecase

import com.kevin7254.blackjack.domain.model.Deck
import com.kevin7254.blackjack.domain.model.Hand


/**
 * Use case responsible for dealing a card to a hand.
 */
class DealCardUseCase {
    operator fun invoke(
        deck: Deck,
        hand: Hand,
        faceUp: Boolean = true,
    ): Pair<Deck, Hand> {
        require(deck.cards.isNotEmpty()) { "Deck is empty" }

        val topCard = deck.cards.first()
        val updatedCard = topCard.copy(isFaceUp = faceUp)

        val updatedHand = hand.addCard(updatedCard)
        val updatedDeck = deck.copy(cards = deck.cards.drop(1))

        return updatedDeck to updatedHand
    }
}

