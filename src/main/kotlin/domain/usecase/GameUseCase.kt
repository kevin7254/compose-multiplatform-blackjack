package domain.usecase

import data.GameState
import domain.model.Deck
import domain.model.Hand
import domain.repository.DeckRepository
import domain.rules.BlackjackRules
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GameUseCase(
    private val deckRepository: DeckRepository,
    private val blackjackRules: BlackjackRules,
) {

    /**
     * Creates a flow that provides a complete, initial game setup.
     */
    fun newGame(): Flow<GameState> = flow {
        val initialDeck = deckRepository.createStandardDeck(shuffle = true)

        // Player's first card
        val (deckAfterP1, playerHand1) = dealCard(initialDeck, Hand())
        // Dealer's first card
        val (deckAfterD1, dealerHand1) = dealCard(deckAfterP1, Hand())
        // Player's second card
        val (deckAfterP2, playerHand2) = dealCard(deckAfterD1, playerHand1)
        // Dealer's second card (face down)
        val (deckAfterD2, dealerHand2) = dealCard(deckAfterP2, dealerHand1, faceUp = false)

        val result = blackjackRules.evaluateResult(playerHand2, dealerHand2)
        val finalState = GameState(deckAfterD2, playerHand2, dealerHand2, result)

        emit(finalState)
    }

    /**
     * Logic for when the player hits.
     */
    fun playerHit(currentState: GameState): GameState {
        val (updatedDeck, updatedHand) = dealCard(currentState.deck, currentState.playerCards)
        val result = blackjackRules.evaluateResult(updatedHand, currentState.dealerCards)
        return currentState.copy(deck = updatedDeck, playerCards = updatedHand, gameResult = result)
    }

    /**
     * Logic for when the player stands.
     */
    fun playerStand(currentState: GameState): GameState {
        // Reveal dealer's face-down card
        val revealedDealerHand = flipDealerCard(currentState.dealerCards)

        // Dealer takes its turn
        val (finalDeck, finalDealerHand) = runDealerTurn(currentState.deck, revealedDealerHand)

        val result = blackjackRules.evaluateResult(currentState.playerCards, finalDealerHand)
        return currentState.copy(deck = finalDeck, dealerCards = finalDealerHand, gameResult = result)
    }

    private fun dealCard(
        deck: Deck,
        hand: Hand,
        faceUp: Boolean = true,
    ): Pair<Deck, Hand> {
        require(deck.cards.isNotEmpty()) { "Deck is empty" }
        val topCard = deck.cards.first().copy(isFaceUp = faceUp)
        val updatedHand = hand.addCard(topCard)
        val updatedDeck = deck.copy(cards = deck.cards.drop(1))
        return updatedDeck to updatedHand
    }

    private fun flipDealerCard(dealerHand: Hand): Hand {
        val updatedCards = dealerHand.cards.mapIndexed { index, card ->
            if (index == 1) card.copy(isFaceUp = true) else card
        }
        return dealerHand.copy(cards = updatedCards)
    }

    private fun runDealerTurn(deck: Deck, dealerHand: Hand): Pair<Deck, Hand> {
        var localDeck = deck
        var localHand = dealerHand
        while (blackjackRules.shouldDealerDraw(localHand)) {
            val (updatedDeck, updatedHand) = dealCard(localDeck, localHand)
            localDeck = updatedDeck
            localHand = updatedHand
        }
        return localDeck to localHand
    }
}
