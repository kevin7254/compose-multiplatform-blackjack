package com.kevin7254.blackjack.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import compose_multiplatform_blackjack.composeapp.generated.resources.JetBrainsMono_Bold
import compose_multiplatform_blackjack.composeapp.generated.resources.JetBrainsMono_BoldItalic
import compose_multiplatform_blackjack.composeapp.generated.resources.JetBrainsMono_Italic
import compose_multiplatform_blackjack.composeapp.generated.resources.JetBrainsMono_Regular
import compose_multiplatform_blackjack.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.Font

object Fonts {

    //TODO update.
    @Composable
    fun typo() = androidx.compose.material3.Typography(
        bodyLarge = TextStyle(
            fontFamily = jetbrainsMono(),
            fontWeight = FontWeight.Normal,
            fontSize = 22.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.5.sp
        ),
        titleLarge = TextStyle(
            fontFamily = jetbrainsMono(),
            fontWeight = FontWeight.Normal,
            fontSize = 28.sp,
            lineHeight = 28.sp,
            letterSpacing = 0.sp
        ),
        labelSmall = TextStyle(
            fontFamily = jetbrainsMono(),
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp
        )
    )
   /* @Composable
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
    )*/

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
