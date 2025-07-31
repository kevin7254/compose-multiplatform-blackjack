package domain.usecase

import domain.model.Hand

class FlipCardUseCase {
    operator fun invoke(hand: Hand, cardIndex: Int): Hand {
        val updatedCards = hand.cards.mapIndexed { index, card ->
            if (index == cardIndex) card.copy(isFaceUp = true) else card
        }
        return hand.copy(cards = updatedCards)
    }
}

