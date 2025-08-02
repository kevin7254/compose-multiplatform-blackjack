package domain.usecase

import domain.model.Deck
import domain.model.GameResult
import domain.model.GameState
import domain.model.Hand
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Use case responsible for game animations.
 * This separates animation logic from game logic, following the Single Responsibility Principle.
 */
class GameAnimationUseCase(
    private val gameUseCase: GameUseCase,
    private val cardDealDelay: Long = 800L,
    private val cardFlipDelay: Long = 1300L,
) {
    /**
     * Creates animated new game flow with progressive card dealing
     */
    fun newGameWithAnimation(): Flow<GameState> = flow {
        // First, get the final state from the core use case
        val finalState = gameUseCase.newGame()

        val initialDeck = finalState.deck
        val playerCards = finalState.playerCards.cards
        val dealerCards = finalState.dealerCards.cards

        // Start with empty hands
        var currentPlayerHand = Hand()
        var currentDealerHand = Hand()

        // Player's first card
        if (playerCards.isNotEmpty()) {
            val firstPlayerCard = playerCards[0]
            currentPlayerHand = currentPlayerHand.addCard(firstPlayerCard)
            emit(createProgressiveState(initialDeck, currentPlayerHand, currentDealerHand))
            delay(cardDealDelay)
        }

        // Dealer's first card
        if (dealerCards.isNotEmpty()) {
            val firstDealerCard = dealerCards[0]
            currentDealerHand = currentDealerHand.addCard(firstDealerCard)
            emit(createProgressiveState(initialDeck, currentPlayerHand, currentDealerHand))
            delay(cardDealDelay)
        }

        // Player's second card
        if (playerCards.size > 1) {
            val secondPlayerCard = playerCards[1]
            currentPlayerHand = currentPlayerHand.addCard(secondPlayerCard)
            emit(createProgressiveState(initialDeck, currentPlayerHand, currentDealerHand))
            delay(cardDealDelay)
        }

        // Dealer's second card (face down)
        if (dealerCards.size > 1) {
            val secondDealerCard = dealerCards[1]
            currentDealerHand = currentDealerHand.addCard(secondDealerCard)
            emit(createProgressiveState(initialDeck, currentPlayerHand, currentDealerHand))
        }
        emit(finalState)
    }

    /**
     * Creates an animated flow for a player hit.
     */
    fun playerHitWithAnimation(currentState: GameState): Flow<GameState> = flow {
        // Calculate the next state immediately
        val nextState = gameUseCase.playerHit(currentState)
        // Emit the new state after a delay for the card animation
        delay(cardDealDelay)
        emit(nextState)
    }

    fun playerStandWithAnimation(currentState: GameState): Flow<GameState> = flow {
        // --- Phase 1: Reveal the dealer's card (orchestration) ---
        val revealedDealerHand = gameUseCase.flipDealerCard(currentState.dealerCards)
        var animationState = currentState.copy(
            dealerCards = revealedDealerHand,
        )
        emit(animationState)
        delay(cardFlipDelay)

        // --- Phase 2: Animate the dealer drawing cards (orchestration) ---
        val dealerTurnSequence = gameUseCase.dealerTurnAsSequence(
            deck = animationState.deck,
            dealerHand = revealedDealerHand
        )

        for ((deckAfterDraw, handAfterDraw) in dealerTurnSequence) {
            animationState = animationState.copy(
                deck = deckAfterDraw,
                dealerCards = handAfterDraw,
            )
            emit(animationState)
            delay(cardDealDelay)
        }

        // --- Phase 3: Emit the final, conclusive state (delegation) ---
        // INSTEAD of calculating the result here, we ask the source of truth.
        // This is the key change.
        val finalState = gameUseCase.playerStand(currentState)
        emit(finalState)
    }

    /**
     * Creates a progressive game state for animation
     */
    private fun createProgressiveState(
        deck: Deck,
        playerHand: Hand,
        dealerHand: Hand,
    ): GameState {
        return GameState(
            deck = deck,
            playerCards = playerHand,
            dealerCards = dealerHand,
            gameResult = GameResult.PLAYING,
        )
    }
}