package presentation.screens.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import domain.model.Card
import presentation.screens.components.CardImageDefaults.BACKGROUND_IMAGE_PATH
import presentation.screens.components.CardImageDefaults.CARD_HEIGHT
import presentation.screens.components.CardImageDefaults.SHAPE

@Composable
fun CardImage(
    card: Card,
) {
    val path = if (card.isFaceUp) "cards/${card.imageName}" else BACKGROUND_IMAGE_PATH
    Image(
        // TODO: Deprecated
        painter = painterResource(path),
        contentDescription = "Card image",
        alignment = Alignment.Center,
        modifier = Modifier
            .height(CARD_HEIGHT)
            .wrapContentWidth()
            .clip(SHAPE),
        contentScale = ContentScale.Fit
    )
}

private object CardImageDefaults {
    val CARD_HEIGHT = 140.dp
    val SHAPE = RoundedCornerShape(12.dp)
    const val BACKGROUND_IMAGE_PATH = "cards/1B.png"
}