package musicapp

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import musicapp.theme.accentColor
import musicapp.theme.darkBackground
import musicapp.theme.darkSurface
import musicapp.theme.selectedItemBackgroundColor
import musicapp.theme.textPrimary

@Composable
internal fun MusicAppTheme(
    content: @Composable () -> Unit
) {
    val colors = darkColors(
        primary = selectedItemBackgroundColor,
        primaryVariant = accentColor,
        secondary = selectedItemBackgroundColor,
        background = darkBackground,
        surface = darkSurface,
        onPrimary = Color.White,
        onSecondary = Color.White,
        onBackground = textPrimary,
        onSurface = textPrimary,
    )

    val typography = Typography(
        h4 = TextStyle(
            fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold, fontSize = 32.sp, color = textPrimary
        ),
        h5 = TextStyle(
            fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold, fontSize = 24.sp, color = textPrimary
        ),
        h6 = TextStyle(
            fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold, fontSize = 20.sp, color = textPrimary
        ),
        subtitle1 = TextStyle(
            fontFamily = FontFamily.Default, fontWeight = FontWeight.Medium, fontSize = 18.sp, color = textPrimary
        ),
        body1 = TextStyle(
            fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal, fontSize = 16.sp, color = textPrimary
        ),
        body2 = TextStyle(
            fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal, fontSize = 14.sp, color = Color(0xFFA0A0A0)
        ),
        caption = TextStyle(
            fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal, fontSize = 12.sp, color = Color(0xFFA0A0A0)
        )
    )
    val shapes = Shapes(
        small = RoundedCornerShape(8.dp),
        medium = RoundedCornerShape(16.dp),
        large = RoundedCornerShape(24.dp)
    )

    MaterialTheme(
        colors = colors, typography = typography, shapes = shapes, content = content
    )
}
