package domain.model

import androidx.compose.ui.graphics.Color
import domain.rules.BlackjackRules

data class GameResultDisplay(
    val message: String,
    val color: Color,
    val isGameOver: Boolean,
)

fun BlackjackRules.GameResult.toDisplay(): GameResultDisplay {
    return when (this) {
        BlackjackRules.GameResult.PLAYING -> GameResultDisplay(
            message = "Game in Progress.",
            color = Color.White,
            isGameOver = false
        )

        BlackjackRules.GameResult.SHOW_DEALER_CARD -> GameResultDisplay(
            message = "Game in Progress.",
            color = Color.White,
            isGameOver = false
        )

        BlackjackRules.GameResult.PLAYER_WINS -> GameResultDisplay(
            message = "You Win!",
            color = Color.Green,
            isGameOver = true
        )

        BlackjackRules.GameResult.PLAYER_WINS_BLACKJACK -> GameResultDisplay(
            message = "BLACKJACK!",
            color = Color(0xFFFFD700), // Gold
            isGameOver = true
        )

        BlackjackRules.GameResult.DEALER_WINS -> GameResultDisplay(
            message = "Dealer Wins.",
            color = Color.Red,
            isGameOver = true
        )

        BlackjackRules.GameResult.TIE -> GameResultDisplay(
            message = "Push.",
            color = Color.Yellow,
            isGameOver = true
        )
    }
}
