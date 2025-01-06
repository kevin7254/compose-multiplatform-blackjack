package domain

import data.Deck
import data.Hand
import data.DeckRepository

class ResetGameUseCase(private val deckRepository: DeckRepository) {
    operator fun invoke(): Deck {
        // Create a fresh deck for the new game
        return deckRepository.createStandardDeck(shuffle = true)
    }
}

class FlipCardUseCase {
    operator fun invoke(hand: Hand, cardIndex: Int): Hand {
        val updatedCards = hand.cards.mapIndexed { index, card ->
            if (index == cardIndex) card.copy(isFaceUp = true) else card
        }
        return hand.copy(cards = updatedCards)
    }
}


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


class DealerTurnUseCase(
    private val dealCardUseCase: DealCardUseCase,
    private val blackjackRules: BlackjackRules,
) {
    operator fun invoke(deck: Deck, dealerHand: Hand): Pair<Deck, Hand> {
        var localDeck = deck
        var localHand = dealerHand
        while (blackjackRules.shouldDealerDraw(localHand)) {
            val (updatedDeck, updatedHand) = dealCardUseCase(localDeck, localHand)
            localDeck = updatedDeck
            localHand = updatedHand
        }
        return localDeck to localHand
    }
}
