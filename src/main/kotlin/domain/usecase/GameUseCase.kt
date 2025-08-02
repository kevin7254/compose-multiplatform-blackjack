package domain.usecase

import domain.model.Deck
import domain.model.GameState
import domain.model.Hand
import domain.repository.DeckRepository
import domain.rules.BlackjackRules

class GameUseCase(
    private val deckRepository: DeckRepository,
    private val blackjackRules: BlackjackRules,
) {

    /**
     * Creates a flow that provides a complete, initial game setup.
     */
    fun newGame(): GameState {
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
        return GameState(deckAfterD2, playerHand2, dealerHand2, result)
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
     * Logic for when the player stands. It computes the final state directly
     * by running the dealer's turn to completion.
     */
    fun playerStand(currentState: GameState): GameState {
        val revealedDealerHand = flipDealerCard(currentState.dealerCards)

        // Run the dealer's turn to the end and get the final deck and hand.
        val (finalDeck, finalDealerHand) = dealerTurnAsSequence(currentState.deck, revealedDealerHand).last()

        val result = blackjackRules.evaluateResult(currentState.playerCards, finalDealerHand)
        return currentState.copy(deck = finalDeck, dealerCards = finalDealerHand, gameResult = result)
    }

    /**
     * Reveals the dealer's face-down card. This is a pure business rule.
     */
    fun flipDealerCard(dealerHand: Hand): Hand {
        // Find the first face-down card and flip it. This is more robust than assuming index 1.
        val cardIndexToFlip = dealerHand.cards.indexOfFirst { !it.isFaceUp }
        if (cardIndexToFlip == -1) return dealerHand // All cards already face up.

        val updatedCards = dealerHand.cards.mapIndexed { index, card ->
            if (index == cardIndexToFlip) card.copy(isFaceUp = true) else card
        }
        return dealerHand.copy(cards = updatedCards)
    }

    /**
     * Provides the dealer's entire turn as a lazy sequence of states (Deck and Hand pairs).
     * This contains the pure logic of the dealer drawing cards, which can be consumed
     * by different components (like an animation use case).
     *
     * @return A Sequence where each element is the next state of the (Deck, Hand) after a draw.
     */
    fun dealerTurnAsSequence(deck: Deck, dealerHand: Hand): Sequence<Pair<Deck, Hand>> = sequence {
        var currentDeck = deck
        var currentHand = dealerHand

        yield(currentDeck to currentHand)

        while (blackjackRules.shouldDealerDraw(currentHand)) {
            val (updatedDeck, updatedHand) = dealCard(currentDeck, currentHand)
            currentDeck = updatedDeck
            currentHand = updatedHand
            yield(currentDeck to currentHand) // Yield the state after each draw.
        }
    }

    /**
     * Deals a single card from the deck to a hand. Made public to be a reusable building block.
     */
    fun dealCard(
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
}
