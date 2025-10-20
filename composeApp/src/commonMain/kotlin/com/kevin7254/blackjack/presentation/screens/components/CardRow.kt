package com.kevin7254.blackjack.presentation.screens.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kevin7254.blackjack.domain.model.Hand
import kotlinx.coroutines.delay
import com.kevin7254.blackjack.presentation.screens.CommonDefaults.LOCAL_DEBUG
import com.kevin7254.blackjack.presentation.screens.components.CardRowDefaults.ANIMATION_DURATION_MS
import com.kevin7254.blackjack.presentation.screens.components.CardRowDefaults.DIVIDER_HEIGHT


@Composable
fun CardRow(hand: Hand) {
    // Number of cards that are currently “dealt” and visible
    var cardsDealt by remember { mutableStateOf(0) }

    LaunchedEffect(hand.cards.size) {
        for (i in cardsDealt until hand.cards.size) {
            // For the first couple of cards, add an extra delay to simulate the initial "deal"
            if (cardsDealt < 2) delay(ANIMATION_DURATION_MS.toLong())
            cardsDealt = i + 1
        }
    }

    AnimatedLazyRow(hand, cardsDealt)

    Spacer(modifier = Modifier.height(DIVIDER_HEIGHT))
    Text(
        text = "Score: ${hand.totalValue()}",
        style = MaterialTheme.typography.bodyMedium,
        color = Color.White,
    )
    Spacer(modifier = Modifier.height(DIVIDER_HEIGHT))
    val isDealerSecondCard = hand.cards.lastOrNull()?.isFaceUp?.not()
    if (LOCAL_DEBUG && isDealerSecondCard == true) {
        Text(
            text = "Face-down card: ${hand.cards.lastOrNull()}",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
        )
    }
}

/**
 * Animates the 'slide-in' of Cards.
 *
 * @param hand The hand that will be animated.
 */
@Composable
fun AnimatedLazyRow(
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
                    animationSpec = tween(ANIMATION_DURATION_MS),
                ) + fadeIn(animationSpec = tween(ANIMATION_DURATION_MS)),
                exit = slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth },
                ) + fadeOut(animationSpec = tween(ANIMATION_DURATION_MS))
            ) { CardImage(card) }
            Spacer(Modifier.width(8.dp))
        }
    }
}

private object CardRowDefaults {
    const val ANIMATION_DURATION_MS = 700
    val DIVIDER_HEIGHT = 8.dp
}