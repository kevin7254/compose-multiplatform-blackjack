package presentation.theme

import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import me.sample.library.resources.*
import org.jetbrains.compose.resources.Font

object Fonts {
    @Composable
    fun customTypography() = Typography(
        defaultFontFamily = jetbrainsMono(),
        h1 = TextStyle(
            fontFamily = jetbrainsMono(),
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
        ),
        h2 = TextStyle(
            fontFamily = jetbrainsMono(),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
        ),
        body1 = TextStyle(
            fontFamily = jetbrainsMono(),
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
        )
    )

    @Composable
    fun jetbrainsMono() = FontFamily(
        Font(
            Res.font.JetBrainsMono_Regular,
            FontWeight.Normal,
            FontStyle.Normal,
        ),
        Font(
            Res.font.JetBrainsMono_Italic,
            FontWeight.Normal,
            FontStyle.Italic,
        ),

        Font(
            Res.font.JetBrainsMono_Bold,
            FontWeight.Bold,
            FontStyle.Normal,
        ),
        Font(
            Res.font.JetBrainsMono_BoldItalic,
            FontWeight.Bold,
            FontStyle.Italic,
        ),
    )
}
