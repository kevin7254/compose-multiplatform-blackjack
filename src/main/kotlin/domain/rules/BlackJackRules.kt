package domain.rules

import domain.model.Hand


class BlackjackRules {
    fun shouldDealerDraw(dealerHand: Hand): Boolean {
        // TODO: soft 17 (ACE)
        return dealerHand.totalValue() < 17
    }

    fun evaluateResult(playerHand: Hand, dealerHand: Hand): GameResult {
        // Handle blackjack and bust cases first
        return when {
            playerHand.isBlackJack -> GameResult.PLAYER_WINS_BLACKJACK
            playerHand.bestValue() > 21 -> GameResult.DEALER_WINS
            dealerHand.cards.run { any { !it.isFaceUp } || size < 2 } -> GameResult.PLAYING // Still playing
            else -> {
                // Calculate totals
                val playerTotal = playerHand.bestValue()
                val dealerTotal = dealerHand.bestValue()

                // Determine results based on busts and totals
                when {
                    playerTotal > 21 && dealerTotal <= 21 -> GameResult.DEALER_WINS
                    dealerTotal > 21 && playerTotal <= 21 -> GameResult.PLAYER_WINS
                    playerTotal > 21 && dealerTotal > 21 -> GameResult.TIE
                    else -> compareTotals(playerTotal, dealerTotal)
                }
            }
        }
    }


    private fun compareTotals(playerTotal: Int, dealerTotal: Int): GameResult {
        return when {
            playerTotal > dealerTotal -> GameResult.PLAYER_WINS
            playerTotal < dealerTotal -> GameResult.DEALER_WINS
            else -> GameResult.TIE
        }
    }

    // TODO better name
    enum class GameResult {
        PLAYING,
        SHOW_DEALER_CARD,
        PLAYER_WINS,
        PLAYER_WINS_BLACKJACK,
        DEALER_WINS,
        TIE;
    }

    private val Hand.isBlackJack
        get() = cards.run { size == 2 && bestValue() == 21 }
}

