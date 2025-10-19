package com.kevin7254.blackjack.domain.usecase

import com.kevin7254.blackjack.di.DefaultDispatcher
import com.kevin7254.blackjack.domain.model.Card
import com.kevin7254.blackjack.domain.model.Deck
import com.kevin7254.blackjack.domain.model.Hand
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt
import kotlin.random.Random

/**
 * The different actions that the player can take in the game.
 */
enum class StrategyAction {
    /**
     * Hit (add another card to the hand).
     */
    HIT,

    /**
     * Stand (no more cards/end the round).
     */
    STAND,

    /**
     * Impossible to win.
     */
    IMPOSSIBLE,

    /**
     * Waiting for the simulation to complete or dealer to draw.
     */
    WAITING,
}

/**
 * The recommended action and reason for the action.
 * The action is a [StrategyAction] and the reason is a human-readable string.
 *
 * The action is always the mathematically best action for the current state of the game.
 * The reason is just for debugging purposes and to explain the decision.
 */
data class StrategyRecommendation(
    val action: StrategyAction,
    val reason: String,
)

/**
 * Calculates the optimal blackjack action (Hit/Stand) for the **current deck composition**
 * using Monte‑Carlo simulation. Runs on the injected [dispatcher] so the UI thread remains free.
 */
class OptimalStrategyUseCase(
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    private val random: Random = Random.Default,
) {

    /**
     * @param playerHand    The player’s current hand
     * @param dealerUpCard  The dealer’s visible card
     * @param currentDeck   The current deck(s)
     * @param upcomingCards Exact sequence of cards that will be drawn next (debug mode)
     * @param simulations   Number of Monte‑Carlo iterations if [upcomingCards] is empty
     */
    operator fun invoke(
        playerHand: Hand,
        dealerUpCard: Card,
        currentDeck: Deck,
        upcomingCards: List<Card> = emptyList(),
        simulations: Int = 10_000,
    ): Flow<StrategyRecommendation> = flow {
        emit(handleTrivialCases(playerHand))
        if (playerHand.bestValue() >= 21) return@flow

        val recommendation = withContext(dispatcher) {
            if (upcomingCards.isNotEmpty()) {
                simulateWithKnownDeck(playerHand, dealerUpCard, currentDeck, upcomingCards)
            } else {
                simulateProbabilistic(playerHand, dealerUpCard, currentDeck, simulations)
            }
        }
        emit(recommendation)
    }.flowOn(dispatcher)

    // Monte-Carlo (unknown deck)
    private fun simulateProbabilistic(
        playerHand: Hand,
        dealerUpCard: Card,
        currentDeck: Deck,
        simulations: Int,
    ): StrategyRecommendation {
        var hitWins = 0
        var standWins = 0
        var pushesHit = 0
        var pushesStand = 0

        repeat(simulations) {
            val shoe = currentDeck.cards.toMutableList().also { deck ->
                deck.removeAll(playerHand.cards)
                deck.remove(dealerUpCard)
            }

            val hitOutcome = simulateSingleRound(
                playerHand.copy(),
                dealerUpCard,
                shoe.toMutableList(),
                drawForPlayer = true,
            )
            when (hitOutcome) {
                Outcome.WIN -> hitWins++
                Outcome.PUSH -> pushesHit++
                else -> Unit
            }

            val standOutcome = simulateSingleRound(
                playerHand.copy(),
                dealerUpCard,
                shoe.toMutableList(),
                drawForPlayer = false,
            )
            when (standOutcome) {
                Outcome.WIN -> standWins++
                Outcome.PUSH -> pushesStand++
                else -> Unit
            }
        }

        val hitProb = (hitWins + pushesHit * 0.5) / simulations
        val standProb = (standWins + pushesStand * 0.5) / simulations

        return buildRecommendation(hitProb, standProb)
    }

    private fun simulateWithKnownDeck(
        playerHand: Hand,
        dealerUpCard: Card,
        currentDeck: Deck,
        upcoming: List<Card>,
    ): StrategyRecommendation {
        val shoe = currentDeck.cards.toMutableList().apply { removeAll(upcoming) }

        val hitOutcome = playDeterministic(
            playerHand.copy(), dealerUpCard, upcoming.toMutableList(), true, shoe.toMutableList()
        )
        val standOutcome = playDeterministic(
            playerHand.copy(), dealerUpCard, upcoming.toMutableList(), false, shoe.toMutableList()
        )

        val hitProb = if (hitOutcome == Outcome.WIN) 1.0 else if (hitOutcome == Outcome.PUSH) 0.5 else 0.0
        val standProb = if (standOutcome == Outcome.WIN) 1.0 else if (standOutcome == Outcome.PUSH) 0.5 else 0.0

        return buildRecommendation(hitProb, standProb)
    }

    private fun buildRecommendation(hitProb: Double, standProb: Double): StrategyRecommendation {
        return when {
            hitProb > standProb -> StrategyRecommendation(
                StrategyAction.HIT,
                "Hit win-rate ${hitProb.asPct1()}% vs Stand ${standProb.asPct1()}%"
            )

            standProb > hitProb -> StrategyRecommendation(
                StrategyAction.STAND,
                "Stand win-rate ${standProb.asPct1()}% vs Hit ${hitProb.asPct1()}%"
            )

            hitProb == 0.0 && standProb == 0.0 -> StrategyRecommendation(
                StrategyAction.IMPOSSIBLE,
                "Cannot win this hand",
            )

            else -> StrategyRecommendation(
                StrategyAction.STAND,
                "Equal outcomes – prefer Stand",
            )
        }
    }

    private fun handleTrivialCases(playerHand: Hand): StrategyRecommendation {
        val total = playerHand.bestValue()
        return when {
            total > 21 -> StrategyRecommendation(StrategyAction.IMPOSSIBLE, "Already busted")
            total == 21 -> StrategyRecommendation(StrategyAction.STAND, "You have 21")
            else -> StrategyRecommendation(StrategyAction.WAITING, "Simulation running…")
        }
    }

    // Simulates one complete round either after hitting once or standing immediately.
    private fun simulateSingleRound(
        playerHand: Hand,
        dealerUpCard: Card,
        shoe: MutableList<Card>,
        drawForPlayer: Boolean,
    ): Outcome {
        val localRandom = random
        var currentPlayerHand = playerHand

        if (drawForPlayer) {
            val card = shoe.random(localRandom).also { shoe.remove(it) }
            currentPlayerHand = currentPlayerHand.addCard(card)
            if (currentPlayerHand.bestValue() > 21) return Outcome.LOSS
        }

        var dealerHand = Hand(listOf(dealerUpCard))
        while (dealerHand.bestValue() < 17 && shoe.isNotEmpty()) {
            val card = shoe.random(localRandom).also { shoe.remove(it) }
            dealerHand = dealerHand.addCard(card)
        }

        return getBestOutcome(currentPlayerHand.bestValue(), dealerHand.bestValue())
    }

    // Deterministic branch when we know the next cards exactly.
    private fun playDeterministic(
        playerHand: Hand,
        dealerUpCard: Card,
        upcoming: MutableList<Card>,
        drawForPlayer: Boolean,
        shoe: MutableList<Card>,
    ): Outcome {
        var currentPlayerHand = playerHand

        if (drawForPlayer) {
            currentPlayerHand = currentPlayerHand.addCard(upcoming.removeFirst())
            if (currentPlayerHand.bestValue() > 21) return Outcome.LOSS
        }

        var dealerHand = Hand(listOf(dealerUpCard))
        while (dealerHand.bestValue() < 17) {
            val nextCard = if (upcoming.isNotEmpty()) upcoming.removeFirst() else shoe.removeFirst()
            dealerHand = dealerHand.addCard(nextCard)
        }

        return getBestOutcome(currentPlayerHand.bestValue(), dealerHand.bestValue())
    }

    private fun getBestOutcome(playerTotal: Int, dealerTotal: Int): Outcome = when {
        playerTotal > 21 -> Outcome.LOSS
        dealerTotal > 21 -> Outcome.WIN
        playerTotal > dealerTotal -> Outcome.WIN
        playerTotal < dealerTotal -> Outcome.LOSS
        else -> Outcome.PUSH
    }

    enum class Outcome { WIN, LOSS, PUSH }
}

private fun Double.asPct1(): String {
    // Convert to percent, keep 1 decimal without using JVM APIs
    val scaled = (this * 1000.0).roundToInt() // = percent * 10
    val whole = scaled / 10
    val frac = scaled % 10
    return "$whole.$frac"
}