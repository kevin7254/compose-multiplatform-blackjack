package com.kevin7254.blackjack.presentation.screens.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kevin7254.blackjack.domain.bank.model.Bankroll
import com.kevin7254.blackjack.domain.bank.model.Chips
import com.kevin7254.blackjack.domain.model.GameResultDisplay
import com.kevin7254.blackjack.domain.model.GameState
import com.kevin7254.blackjack.domain.model.RoundPhase
import com.kevin7254.blackjack.domain.model.toDisplay
import com.kevin7254.blackjack.domain.usecase.StrategyAction
import com.kevin7254.blackjack.domain.usecase.StrategyRecommendation
import com.kevin7254.blackjack.presentation.screens.CommonDefaults.LOCAL_DEBUG
import com.kevin7254.blackjack.presentation.screens.components.GameTableDefaults.ACCENT_COLOR
import com.kevin7254.blackjack.presentation.screens.components.GameTableDefaults.PADDING

@Composable
fun GameTable(
    gameState: GameState,
    roundPhase: RoundPhase,
    playerChips: Bankroll,
    strategyRecommendation: StrategyRecommendation,
    onPlayerHit: () -> Unit,
    onPlayerStand: () -> Unit,
    onNewGame: () -> Unit,
    onChipClicked: (Int) -> Unit,
    onDeal: () -> Unit,
    chipState: List<Chips>,
    onBettedChipClicked: () -> Unit,
) {
    val gameResultDisplay = toDisplay(gameState.status)
    val isGameOver = gameResultDisplay.isGameOver
    val isPlacingBet = roundPhase == RoundPhase.PlacingBet

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        TopHalf(
            gameState = gameState,
            roundPhase = roundPhase,
            strategyRecommendation = strategyRecommendation,
            isGameOver = isGameOver,
            modifier = Modifier.weight(0.5f).fillMaxWidth()
        )

        if (isPlacingBet.not()) {
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                thickness = 1.dp,
                color = Color.White,
            )
        }

        if (isPlacingBet) {
            Text(
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxWidth()
                    .padding(PADDING),
                text = "Place your bets.",
                style = MaterialTheme.typography.displayLarge,
                color = Color.White,
                textAlign = TextAlign.Center,
            )
            if (chipState.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .padding(vertical = PADDING)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    ChipImage(chip = chipState.last().amount, onChipClicked = {
                        onBettedChipClicked.invoke()
                    })
                }
            }
        }

        BottomHalf(
            isPlacingBet = isPlacingBet,
            gameState = gameState,
            roundPhase = roundPhase,
            playerChips = playerChips,
            gameResultDisplay = gameResultDisplay,
            strategyRecommendation = strategyRecommendation,
            onPlayerHit = onPlayerHit,
            onPlayerStand = onPlayerStand,
            onNewGame = onNewGame,
            onChipClicked = onChipClicked,
            onDeal = onDeal,
            modifier = Modifier.weight(0.5f).fillMaxWidth()
        )
    }
}

@Composable
private fun TopHalf(
    gameState: GameState,
    roundPhase: RoundPhase,
    strategyRecommendation: StrategyRecommendation,
    isGameOver: Boolean,
    modifier: Modifier = Modifier,
) {
    if (roundPhase == RoundPhase.PlacingBet) {
        return
    }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        if (LOCAL_DEBUG && !isGameOver) {
            StrategyRecommendationTopCard(strategyRecommendation = strategyRecommendation)
            Spacer(Modifier.height(8.dp))
        }
        DealerSection(gameState = gameState)
    }
}

@Composable
private fun BottomHalf(
    gameState: GameState,
    roundPhase: RoundPhase,
    playerChips: Bankroll,
    gameResultDisplay: GameResultDisplay,
    strategyRecommendation: StrategyRecommendation,
    onPlayerHit: () -> Unit,
    onPlayerStand: () -> Unit,
    onNewGame: () -> Unit,
    onChipClicked: (Int) -> Unit,
    onDeal: () -> Unit,
    modifier: Modifier = Modifier,
    isPlacingBet: Boolean,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
    ) {
        GameResultSection(gameResultDisplay = gameResultDisplay)
        PlayerSection(gameState = gameState, isPlacingBet = isPlacingBet)
        PlayerButtons(
            gameState = gameState,
            roundPhase = roundPhase,
            gameResultDisplay = gameResultDisplay,
            strategyRecommendation = strategyRecommendation,
            onPlayerHit = onPlayerHit,
            onPlayerStand = onPlayerStand,
            onNewGame = onNewGame,
            onDeal = onDeal,
        )

        ChipsBox(
            onChipClicked = onChipClicked,
            amountOfChips = playerChips.balance.amount,
            enabled = roundPhase == RoundPhase.PlacingBet,
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
        colors = CardDefaults.cardColors(
            containerColor = when (strategyRecommendation.action) {
                StrategyAction.HIT -> Color(0xFF4CAF50) // Green
                StrategyAction.STAND -> ACCENT_COLOR
                StrategyAction.IMPOSSIBLE -> Color(0xFFFF5722) // Red
                StrategyAction.WAITING -> Color(0xFF795548)
            }
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🎯 OPTIMAL STRATEGY",
                color = Color.White,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = strategyRecommendation.action.name,
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = strategyRecommendation.reason,
                color = Color.White,
                style = MaterialTheme.typography.bodySmall,
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
        style = MaterialTheme.typography.titleLarge,
        color = Color.White,
    )
    Spacer(Modifier.height(8.dp))
    CardRow(hand = gameState.dealerCards)
}

@Composable
private fun PlayerSection(
    gameState: GameState,
    isPlacingBet: Boolean,
) {
    if (isPlacingBet) {
        return
    }
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
            colors = CardDefaults.cardColors(containerColor = Color.Black),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = gameResultDisplay.message,
                color = gameResultDisplay.color,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier.padding(PADDING)
            )
        }
    }
    Spacer(Modifier.height(PADDING))
}

@Composable
private fun PlayerButtons(
    gameState: GameState,
    roundPhase: RoundPhase,
    gameResultDisplay: GameResultDisplay,
    strategyRecommendation: StrategyRecommendation?,
    onPlayerHit: () -> Unit,
    onPlayerStand: () -> Unit,
    onNewGame: () -> Unit,
    onDeal: () -> Unit,
) {
    val isPlacingBet = roundPhase == RoundPhase.PlacingBet
    val isGameOver = gameResultDisplay.isGameOver

    when {
        isPlacingBet -> {
            Button(
                onClick = onDeal,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                modifier = Modifier
                    .width(250.dp)
                    .height(50.dp)
            ) {
                Text("Deal", style = MaterialTheme.typography.titleMedium)
            }
        }

        isGameOver -> {
            Button(
                onClick = onNewGame,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(50.dp)
            ) {
                Text("New Game", style = MaterialTheme.typography.titleMedium)
            }
        }

        else -> {
            // Show Hit/Stand buttons when the game is active with strategy highlighting
            Row(horizontalArrangement = Arrangement.spacedBy(PADDING)) {
                val hitModifier = if (LOCAL_DEBUG && strategyRecommendation?.action == StrategyAction.HIT) {
                    Modifier
                        .height(50.dp)
                        .border(3.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                } else {
                    Modifier.height(50.dp)
                }

                val standModifier = if (LOCAL_DEBUG && strategyRecommendation?.action == StrategyAction.STAND) {
                    Modifier
                        .height(50.dp)
                        .border(3.dp, ACCENT_COLOR, RoundedCornerShape(8.dp))
                } else {
                    Modifier.height(50.dp)
                }

                Button(
                    onClick = onPlayerHit,
                    shape = RoundedCornerShape(8.dp), // ideally from theme
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (LOCAL_DEBUG && strategyRecommendation?.action == StrategyAction.HIT)
                            MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (LOCAL_DEBUG && strategyRecommendation?.action == StrategyAction.HIT)
                            MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                    modifier = hitModifier,
                ) {
                    Text("Hit", style = MaterialTheme.typography.bodyMedium)
                }
                Button(
                    onClick = onPlayerStand,
                    shape = RoundedCornerShape(8.dp), // ideally from theme
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (LOCAL_DEBUG && strategyRecommendation?.action == StrategyAction.STAND)
                            ACCENT_COLOR else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (LOCAL_DEBUG && strategyRecommendation?.action == StrategyAction.STAND)
                            Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                    modifier = standModifier,
                ) {
                    Text("Stand", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }

    if (LOCAL_DEBUG) {
        LazyRow(
            modifier = Modifier
                .padding(top = PADDING)
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.Center,
        ) {
            itemsIndexed(
                items = gameState.deck.cards,
                key = { index, card -> "${card.toInt()}_$index" } // index to be unique across sessions
            ) { index, card ->
                Text(
                    text = "$index: $card",
                    color = Color.White,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    modifier = Modifier.padding(horizontal = 4.dp),
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun ChipsBox(
    onChipClicked: (Int) -> Unit,
    amountOfChips: Int,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    chips: List<Int> = listOf(1, 5, 25, 50, 100, 500, 1000),
) {
    if (!enabled) {
        return
    }

    Box(
        modifier = modifier
            .padding(vertical = PADDING)
            .wrapContentSize(),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(PADDING),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            chips.forEach { chip ->
                ChipImage(chip = chip, onChipClicked = { onChipClicked.invoke(chip) })
            }
            // TODO: Jumps if number changes
            Text(
                text = "$$amountOfChips",
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private object GameTableDefaults {
    val PADDING = 6.dp
    val ACCENT_COLOR = Color(0xFF2196F3)
}