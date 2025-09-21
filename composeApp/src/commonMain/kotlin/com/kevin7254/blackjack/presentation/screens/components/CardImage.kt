package com.kevin7254.blackjack.presentation.screens.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.kevin7254.blackjack.domain.model.Card
import compose_multiplatform_blackjack.composeapp.generated.resources.Res
import compose_multiplatform_blackjack.composeapp.generated.resources._1B
import compose_multiplatform_blackjack.composeapp.generated.resources.allDrawableResources
import org.jetbrains.compose.resources.painterResource

@Composable
fun CardImage(
    card: Card,
) {

    val fileName = if (card.isFaceUp) {
        card.imageName
    } else {
        "1B"
    }

    // TODO a bit nasty might be better ways.. key is "_2D", "_2H" etc.
    val drawableResource = Res.allDrawableResources[fileName]

    val painter = painterResource(drawableResource ?: Res.drawable._1B)

    Image(
        painter = painter,
        contentDescription = "Card image",
        alignment = Alignment.Center,
        modifier = Modifier
            .height(CardImageDefaults.CARD_HEIGHT)
            .wrapContentWidth()
            .clip(CardImageDefaults.SHAPE),
        contentScale = ContentScale.Fit
    )
}

private object CardImageDefaults {
    val CARD_HEIGHT = 140.dp
    val SHAPE = RoundedCornerShape(12.dp)
}