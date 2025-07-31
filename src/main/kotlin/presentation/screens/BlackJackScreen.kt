package presentation.screens

import presentation.viewmodel.CardViewModel
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import data.GameState
import domain.rules.BlackjackRules
import domain.model.Card
import domain.model.Hand
import kotlinx.coroutines.delay
import org.koin.compose.koinInject
import presentation.theme.Fonts
import presentation.viewmodel.BlackjackUiState

@Composable
fun MyApp(content: @Composable () -> Unit) {
    MaterialTheme(
        typography = Fonts.customTypography(),
        content = content,
    )
}

@Composable
fun BlackjackScreen(
    viewModel: CardViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2E7D32)),
        contentAlignment = Alignment.Center
    ) {
        when (val state = uiState) {
            is BlackjackUiState.Loading -> {
                CircularProgressIndicator(color = Color.White)
            }
            is BlackjackUiState.Error -> {
                Text(
                    text = "Error: ${state.message}",
                    color = Color.Red,
                    style = MaterialTheme.typography.h5
                )
            }
            is BlackjackUiState.Success -> {
                GameTable(
                    gameState = state.gameState,
                    onPlayerHit = viewModel::onPlayerHit,
                    onPlayerStand = viewModel::onPlayerStand
                )
            }
        }
    }
}

@Composable
fun GameTable(
    gameState: GameState,
    onPlayerHit: () -> Unit,
    onPlayerStand: () -> Unit
) {
    // Main layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Dealer Section
        Text(
            "Dealer",
            style = MaterialTheme.typography.h1,
            color = Color.White,
        )
        Spacer(Modifier.height(8.dp))

        CardRow(hand = gameState.dealerCards)

        Spacer(Modifier.height(16.dp))

        // Divider
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.White)
        )
        Spacer(Modifier.height(16.dp))

        // Player Section
        Text(
            "Player",
            style = MaterialTheme.typography.h1,
            color = Color.White,
        )
        Spacer(Modifier.height(8.dp))
        CardRow(hand = gameState.playerCards)

        Spacer(Modifier.height(16.dp))

        // Game Result
        Text(
            gameState.gameResult.name,
            color = Color.Yellow,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h2,
        )
        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = onPlayerHit,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.White,
                    disabledBackgroundColor = Color.LightGray,
                    contentColor = Color.Black,
                    disabledContentColor = Color.DarkGray,
                ),
                enabled = gameState.gameResult == BlackjackRules.GameResult.PLAYING
            ) {
                Text("Hit", color = Color.Black)
            }
            Button(
                onClick = onPlayerStand,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.White,
                    disabledBackgroundColor = Color.LightGray,
                    contentColor = Color.Black,
                    disabledContentColor = Color.DarkGray,
                ),
                enabled = gameState.gameResult == BlackjackRules.GameResult.PLAYING
            ) {
                Text("Stand", color = Color.Black)
            }
        }
    }
}


@Composable
fun CardRow(hand: Hand) {
    // Number of cards that are currently “dealt” and visible
    var cardsDealt by remember { mutableStateOf(0) }
    var displayedScore by remember { mutableStateOf(0) }

    LaunchedEffect(hand.cards.size) {
        for (i in cardsDealt until hand.cards.size) {
            // For the first couple of cards, add an extra delay to simulate the initial "deal"
            if (cardsDealt < 2) delay(ANIMATION_DURATION.toLong())
            cardsDealt = i + 1
        }
    }

    AnimatedLazyRow(hand, cardsDealt)

    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "Score: $displayedScore",
        style = MaterialTheme.typography.body1,
        color = Color.White,
    )

    displayedScore = hand.totalValue()
}

/**
 * Animates the 'slide-in' of Cards.
 *
 * @param hand The hand that will be animated.
 */
@Composable
private fun AnimatedLazyRow(
    hand: Hand,
    cardsDealt: Int,
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.Center,
    ) {
        itemsIndexed(
            items = hand.cards,
            key = { _, card -> card.imageName },
        ) { index, card ->
            // Only show the card if it’s in the dealt range
            AnimatedVisibility(
                visible = index < cardsDealt,
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItem(fadeInSpec = null, fadeOutSpec = null),
                enter = slideInHorizontally(
                    initialOffsetX = { fullWidth -> -fullWidth + 1000 },
                    animationSpec = tween(ANIMATION_DURATION),
                ) + fadeIn(animationSpec = tween(ANIMATION_DURATION)),
                exit = slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth },
                ) + fadeOut(animationSpec = tween(ANIMATION_DURATION))
            ) { CardImage(card) }
            Spacer(Modifier.width(8.dp))
        }
    }
}


@Composable
fun CardImage(
    card: Card,
) {
    val path = if (card.isFaceUp) "cards/${card.imageName}" else "cards/1B.png"
    Image(
        painter = painterResource(path),
        contentDescription = "Card image",
        alignment = Alignment.Center,
        modifier = Modifier
            .height(140.dp)
            .wrapContentWidth()
            .clip(RoundedCornerShape(12.dp)),
        contentScale = ContentScale.Fit
    )
}

private const val ANIMATION_DURATION = 700