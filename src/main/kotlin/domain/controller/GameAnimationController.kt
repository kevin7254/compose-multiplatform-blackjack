package domain.controller

import data.GameState
import domain.model.Hand
import domain.rules.BlackjackRules
import domain.usecase.GameUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class GameAnimationController(
    private val gameUseCase: GameUseCase,
    private val cardDealDelay: Long = 800L,
    private val cardFlipDelay: Long = 1300L,
) {

    /**
     * Creates animated new game flow with progressive card dealing
     */
    fun newGameWithAnimation(): Flow<GameState> = flow {
        val finalState = gameUseCase.newGame().first()

        val initialDeck = finalState.deck
        val playerCards = finalState.playerCards.cards
        val dealerCards = finalState.dealerCards.cards

        // Start with empty hands
        var currentPlayerHand = Hand()
        var currentDealerHand = Hand()

        // Player's first card
        if (playerCards.isNotEmpty()) {
            currentPlayerHand = currentPlayerHand.addCard(playerCards[0])
            emit(createProgressiveState(initialDeck, currentPlayerHand, currentDealerHand))
            delay(cardDealDelay)
        }

        // Dealer's first card
        if (dealerCards.isNotEmpty()) {
            currentDealerHand = currentDealerHand.addCard(dealerCards[0])
            emit(createProgressiveState(initialDeck, currentPlayerHand, currentDealerHand))
            delay(cardDealDelay)
        }

        // Player's second card
        if (playerCards.size > 1) {
            currentPlayerHand = currentPlayerHand.addCard(playerCards[1])
            emit(createProgressiveState(initialDeck, currentPlayerHand, currentDealerHand))
            delay(cardDealDelay)
        }

        // Dealer's second card (face down)
        if (dealerCards.size > 1) {
            currentDealerHand = currentDealerHand.addCard(dealerCards[1])
            emit(createProgressiveState(initialDeck, currentPlayerHand, currentDealerHand))
        }
    }

    /**
     * Animates the dealer's turn when player stands
     */
    fun playerStandWithAnimation(currentState: GameState): Flow<GameState> = flow {
        val finalState = gameUseCase.playerStand(currentState)

        // TODO logic can be improved. Same state so dont need to do it twice.
        var animationState = currentState

        val revealedDealerCards = flipDealerCardForAnimation(animationState.dealerCards)
        animationState = animationState.copy(
            dealerCards = revealedDealerCards,
            gameResult = BlackjackRules.GameResult.SHOW_DEALER_CARD,
        )
        emit(animationState)
        delay(cardFlipDelay)

        val finalDealerCards = finalState.dealerCards.cards
        val currentDealerCards = revealedDealerCards.cards

        if (finalDealerCards.size > currentDealerCards.size) {
            var progressiveDealerHand = revealedDealerCards

            for (i in currentDealerCards.size until finalDealerCards.size) {
                progressiveDealerHand = progressiveDealerHand.addCard(finalDealerCards[i])
                animationState = animationState.copy(
                    gameResult = BlackjackRules.GameResult.PLAYING,
                    dealerCards = progressiveDealerHand,
                    deck = finalState.deck // Use final deck state
                )
                emit(animationState)

                // Don't delay after the last card
                if (i < finalDealerCards.size - 1) {
                    delay(cardDealDelay)
                }
            }
        }
        emit(finalState)
    }


    private fun createProgressiveState(
        deck: domain.model.Deck,
        playerHand: Hand,
        dealerHand: Hand,
    ): GameState {
        return GameState(
            deck = deck,
            playerCards = playerHand,
            dealerCards = dealerHand,
            gameResult = BlackjackRules.GameResult.PLAYING,
        )
    }

    // TODO: Can probably remove this. Handled already.
    private fun flipDealerCardForAnimation(dealerHand: Hand): Hand {
        val updatedCards = dealerHand.cards.mapIndexed { index, card ->
            if (index == 1 && !card.isFaceUp) card.copy(isFaceUp = true) else card
        }
        return dealerHand.copy(cards = updatedCards)
    }

}