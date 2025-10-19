package com.kevin7254.blackjack.domain.model

import androidx.compose.ui.graphics.Color
import com.kevin7254.blackjack.domain.bank.model.GameOutcome

/**
 * Game result display data class.
 * @param message The message to display.
 * @param color The color of the message.
 * @param isGameOver Whether the game is over.
 */
data class GameResultDisplay(
    val message: String,
    val color: Color,
    val isGameOver: Boolean,
)

/**
 * Converts a [GameOutcome] to a [GameResultDisplay].
 */
fun GameOutcome.toDisplay(): GameResultDisplay {
    return when (this) {
        GameOutcome.Playing -> GameResultDisplay(
            message = "Game in Progress.",
            color = Color.White,
            isGameOver = false
        )

        GameOutcome.PlayerWin -> GameResultDisplay(
            message = "You Win!",
            color = Color.Green,
            isGameOver = true
        )

        GameOutcome.PlayerBlackJack -> GameResultDisplay(
            message = "BLACKJACK!",
            color = Color(0xFFFFD700), // Gold
            isGameOver = true
        )

        GameOutcome.DealerWin -> GameResultDisplay(
            message = "Dealer Wins.",
            color = Color.Red,
            isGameOver = true
        )

        GameOutcome.Push -> GameResultDisplay(
            message = "Push.",
            color = Color.Yellow,
            isGameOver = true
        )

        GameOutcome.PlayerBust -> GameResultDisplay(
            message = "You Bust!",
            color = Color.Red,
            isGameOver = true,
        )

        GameOutcome.DealerWinAndBlackJack -> GameResultDisplay(
            message = "Dealer Wins and you have BLACKJACK!", //TODO?
            color = Color(0xFFFFD700),
            isGameOver = true,
        )
    }
}
