package com.kevin7254.blackjack.domain.rules

import com.kevin7254.blackjack.domain.bank.model.GameOutcome
import com.kevin7254.blackjack.domain.model.Hand


/**
 * The rules of the Blackjack game.
 * Handles logic such as when a dealer should draw a card and how to evaluate a hand.
 */
class BlackjackRules {
    /**
     * Returns true if the dealer should draw a card given the current hand.
     */
    fun shouldDealerDraw(dealerHand: Hand): Boolean {
        // TODO: soft 17 (ACE)
        return dealerHand.totalValue() < 17
    }

    /**
     * Evaluates the outcome of a hand given the player's hand and dealer's hand.
     */
    fun evaluateResult(playerHand: Hand, dealerHand: Hand): GameOutcome {
        // Handle blackjack and bust cases first
        return when {
            playerHand.isBlackJack -> GameOutcome.PlayerBlackJack
            playerHand.bestValue() > 21 -> GameOutcome.DealerWin
            dealerHand.cards.run { any { !it.isFaceUp } || size < 2 } -> GameOutcome.Playing // Still playing
            else -> {
                // Calculate totals
                val playerTotal = playerHand.bestValue()
                val dealerTotal = dealerHand.bestValue()

                // Determine results based on busts and totals
                when {
                    playerTotal > 21 && dealerTotal <= 21 -> GameOutcome.DealerWin
                    dealerTotal > 21 && playerTotal <= 21 -> GameOutcome.PlayerWin
                    playerTotal > 21 && dealerTotal > 21 -> GameOutcome.Push
                    else -> compareTotals(playerTotal, dealerTotal)
                }
            }
        }
    }

    private fun compareTotals(playerTotal: Int, dealerTotal: Int): GameOutcome {
        return when {
            playerTotal > dealerTotal -> GameOutcome.PlayerWin
            playerTotal < dealerTotal -> GameOutcome.DealerWin
            else -> GameOutcome.Push
        }
    }

    private val Hand.isBlackJack
        get() = cards.run { size == 2 && bestValue() == 21 }
}

