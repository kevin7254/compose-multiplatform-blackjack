package data

import domain.rules.BlackjackRules
import domain.model.Deck
import domain.model.Hand

data class GameState(
    val deck: Deck = Deck(),
    val playerCards: Hand = Hand(),
    val dealerCards: Hand = Hand(),
    val gameResult: BlackjackRules.GameResult = BlackjackRules.GameResult.PLAYING,
)
