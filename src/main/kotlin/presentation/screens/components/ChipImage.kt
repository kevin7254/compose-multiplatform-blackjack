package presentation.screens.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import me.sample.library.resources.Res.getUri
import org.jetbrains.compose.resources.ExperimentalResourceApi

@Composable
fun ChipImage(
    modifier: Modifier = Modifier,
    chip: Int,
    contentDescription: String = "Chip $chip",
) {
    val model = getUri("drawable/chips/chip_${chip}.svg")

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        AsyncImage(
            model = model,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize()
        )
        // Number overlay — pick color for contrast against each chip color
        val numberColor = when (chip) {
            // White needs
            1 -> Color(0xFF222222)
            5, 25, 50, 100, 500 -> Color.White
            1000 -> Color(0xFF222222)
            else -> Color.White
        }
        Text(
            text = chip.toString(),
            color = numberColor,
            // 40.dp chip is tiny; use compact but legible size
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
