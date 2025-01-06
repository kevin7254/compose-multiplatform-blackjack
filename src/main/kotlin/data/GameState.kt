package data

import domain.BlackjackRules

data class GameState(
    val deck: Deck,
    val playerCards: Hand,
    val dealerCards: Hand,
    val gameResult: BlackjackRules.GameResult = BlackjackRules.GameResult.PLAYING,
)