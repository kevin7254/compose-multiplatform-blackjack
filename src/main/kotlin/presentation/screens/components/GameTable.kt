package presentation.screens.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import domain.model.GameResultDisplay
import domain.model.GameState
import domain.model.toDisplay
import domain.usecase.StrategyAction
import domain.usecase.StrategyRecommendation
import presentation.screens.CommonDefaults.DEBUG
import presentation.screens.components.GameTableDefaults.ACCENT_COLOR
import presentation.screens.components.GameTableDefaults.DIVIDER_HEIGHT
import presentation.screens.components.GameTableDefaults.PADDING


@Composable
fun GameTable(
    gameState: GameState,
    strategyRecommendation: StrategyRecommendation,
    onPlayerHit: () -> Unit,
    onPlayerStand: () -> Unit,
    onNewGame: () -> Unit,
) {
    val gameResultDisplay = gameState.gameResult.toDisplay()
    val isGameOver = gameResultDisplay.isGameOver

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TopHalf(
            gameState = gameState,
            strategyRecommendation = strategyRecommendation,
            isGameOver = isGameOver,
            modifier = Modifier.weight(1f).fillMaxWidth()
        )

        Divider(
            color = Color.White,
            thickness = 1.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )

        BottomHalf(
            gameState = gameState,
            gameResultDisplay = gameResultDisplay,
            strategyRecommendation = strategyRecommendation,
            onPlayerHit = onPlayerHit,
            onPlayerStand = onPlayerStand,
            onNewGame = onNewGame,
            modifier = Modifier.weight(1f).fillMaxWidth()
        )
    }
}

@Composable
private fun TopHalf(
    gameState: GameState,
    strategyRecommendation: StrategyRecommendation,
    isGameOver: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        if (DEBUG && !isGameOver) {
            StrategyRecommendationTopCard(strategyRecommendation = strategyRecommendation)
            Spacer(Modifier.height(8.dp))
        }
        DealerSection(gameState = gameState)
    }
}

@Composable
private fun BottomHalf(
    gameState: GameState,
    gameResultDisplay: GameResultDisplay,
    strategyRecommendation: StrategyRecommendation,
    onPlayerHit: () -> Unit,
    onPlayerStand: () -> Unit,
    onNewGame: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        PlayerSection(gameState = gameState)
        GameResultSection(gameResultDisplay = gameResultDisplay)
        PlayerButtons(
            gameState = gameState,
            gameResultDisplay = gameResultDisplay,
            strategyRecommendation = strategyRecommendation,
            onPlayerHit = onPlayerHit,
            onPlayerStand = onPlayerStand,
            onNewGame = onNewGame,
        )
    }
}


@Composable
private fun StrategyRecommendationTopCard(
    strategyRecommendation: StrategyRecommendation,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = PADDING),
        backgroundColor = when (strategyRecommendation.action) {
            StrategyAction.HIT -> Color(0xFF4CAF50) // Green
            StrategyAction.STAND -> ACCENT_COLOR
            StrategyAction.IMPOSSIBLE -> Color(0xFFFF5722) // Red
            StrategyAction.WAITING -> Color(0xFF795548)
        },
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🎯 OPTIMAL STRATEGY",
                color = Color.White,
                style = MaterialTheme.typography.subtitle2,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = strategyRecommendation.action.name,
                color = Color.White,
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = strategyRecommendation.reason,
                color = Color.White,
                style = MaterialTheme.typography.caption,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun DealerSection(
    gameState: GameState,
) {
    // Dealer Section
    Text(
        "Dealer",
        style = MaterialTheme.typography.h1,
        color = Color.White,
    )
    Spacer(Modifier.height(8.dp))
    CardRow(hand = gameState.dealerCards)
}

@Composable
private fun PlayerSection(
    gameState: GameState,
) {
    Text(
        "Player",
        style = MaterialTheme.typography.h1,
        color = Color.White,
    )
    CardRow(hand = gameState.playerCards)
}

@Composable
private fun GameResultSection(
    gameResultDisplay: GameResultDisplay,
) {
    AnimatedVisibility(
        visible = gameResultDisplay.isGameOver,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut()
    ) {
        Card(
            modifier = Modifier.padding(8.dp),
            backgroundColor = Color.Black,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = gameResultDisplay.message,
                color = gameResultDisplay.color,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h2,
                modifier = Modifier.padding(PADDING)
            )
        }
    }
    Spacer(Modifier.height(PADDING))
}

@Composable
private fun PlayerButtons(
    gameState: GameState,
    gameResultDisplay: GameResultDisplay,
    strategyRecommendation: StrategyRecommendation?,
    onPlayerHit: () -> Unit,
    onPlayerStand: () -> Unit,
    onNewGame: () -> Unit,
) {
    // Action Buttons
    if (gameResultDisplay.isGameOver) {
        // Show New Game button when game is over
        Button(
            onClick = onNewGame,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFF4CAF50), // Green
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(50.dp)
        ) {
            Text("New Game", style = MaterialTheme.typography.button)
        }
    } else {
        // Show Hit/Stand buttons when game is active with strategy highlighting
        Row(horizontalArrangement = Arrangement.spacedBy(PADDING)) {
            val hitModifier = if (DEBUG && strategyRecommendation?.action == StrategyAction.HIT) {
                Modifier
                    .height(50.dp)
                    .border(3.dp, Color(0xFF4CAF50), RoundedCornerShape(4.dp))
            } else {
                Modifier.height(50.dp)
            }

            val standModifier = if (DEBUG && strategyRecommendation?.action == StrategyAction.STAND) {
                Modifier
                    .height(50.dp)
                    .border(3.dp, ACCENT_COLOR, RoundedCornerShape(4.dp))
            } else {
                Modifier.height(50.dp)
            }

            Button(
                onClick = onPlayerHit,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (DEBUG && strategyRecommendation?.action == StrategyAction.HIT)
                        Color(0xFF4CAF50) else Color.White,
                    contentColor = if (DEBUG && strategyRecommendation?.action == StrategyAction.HIT)
                        Color.White else Color.Black,
                ),
                modifier = hitModifier
            ) {
                Text("Hit", style = MaterialTheme.typography.button)
            }
            Button(
                onClick = onPlayerStand,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (DEBUG && strategyRecommendation?.action == StrategyAction.STAND)
                        ACCENT_COLOR else Color.White,
                    contentColor = if (DEBUG && strategyRecommendation?.action == StrategyAction.STAND)
                        Color.White else Color.Black,
                ),
                modifier = standModifier
            ) {
                Text("Stand", style = MaterialTheme.typography.button)
            }
        }
    }

    if (DEBUG) {
        LazyRow(
            modifier = Modifier
                .padding(top = PADDING)
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.Center,
        ) {
            itemsIndexed(
                items = gameState.deck.cards,
                key = { index, card -> "${card.toInt()}_$index" } //index to be unique across sessions
            ) { index, card ->
                Text(
                    text = "$index: $card",
                    color = Color.White,
                    fontSize = MaterialTheme.typography.caption.fontSize,
                    modifier = Modifier.padding(horizontal = 4.dp),
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

object GameTableDefaults {
    val DIVIDER_HEIGHT = 16.dp
    val PADDING = 16.dp
    val ACCENT_COLOR = Color(0xFF2196F3)
}