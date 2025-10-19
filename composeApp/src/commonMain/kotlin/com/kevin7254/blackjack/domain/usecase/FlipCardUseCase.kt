package com.kevin7254.blackjack.domain.usecase

import com.kevin7254.blackjack.domain.model.Hand

/**
 * Use case responsible for flipping a card.
 */
class FlipCardUseCase {
    operator fun invoke(hand: Hand, cardIndex: Int): Hand {
        val updatedCards = hand.cards.mapIndexed { index, card ->
            if (index == cardIndex) card.copy(isFaceUp = true) else card
        }
        return hand.copy(cards = updatedCards)
    }
}

