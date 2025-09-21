package com.kevin7254.blackjack.presentation.screens.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import compose_multiplatform_blackjack.composeapp.generated.resources.Res.getUri

@Composable
fun ChipImage(
    modifier: Modifier = Modifier,
    chip: Int,
    contentDescription: String = "Chip $chip",
) {
    // TODO: Convert to compile time resource and remove coil dep?
    val model = getUri("files/chip_${chip}.svg")

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        AsyncImage(
            model = model,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize()
        )
        val numberColor = when (chip) {
            // 1 has white BG, black text
            1 -> Color(0xFF222222)
            5, 25, 50, 100, 500 -> Color.White
            // 1000 has white BG, black text
            1000 -> Color(0xFF222222)
            else -> Color.White
        }
        Text(
            text = chip.toString(),
            color = numberColor,
            //TODO: Use theme instead.
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
