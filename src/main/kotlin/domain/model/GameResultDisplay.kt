package domain.model

import androidx.compose.ui.graphics.Color

data class GameResultDisplay(
    val message: String,
    val color: Color,
    val isGameOver: Boolean,
)

fun GameResult.toDisplay(): GameResultDisplay {
    return when (this) {
        GameResult.PLAYING -> GameResultDisplay(
            message = "Game in Progress.",
            color = Color.White,
            isGameOver = false
        )

        GameResult.PLAYER_WINS -> GameResultDisplay(
            message = "You Win!",
            color = Color.Green,
            isGameOver = true
        )

        GameResult.PLAYER_WINS_BLACKJACK -> GameResultDisplay(
            message = "BLACKJACK!",
            color = Color(0xFFFFD700), // Gold
            isGameOver = true
        )

        GameResult.DEALER_WINS -> GameResultDisplay(
            message = "Dealer Wins.",
            color = Color.Red,
            isGameOver = true
        )

        GameResult.TIE -> GameResultDisplay(
            message = "Push.",
            color = Color.Yellow,
            isGameOver = true
        )
    }
}
