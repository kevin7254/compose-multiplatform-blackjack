package domain.usecase

import domain.model.Deck
import domain.model.Hand


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

