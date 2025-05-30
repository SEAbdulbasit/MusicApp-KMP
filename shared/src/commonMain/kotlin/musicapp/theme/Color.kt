package musicapp.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Primary colors
val lightPrimaryLighter = Color(0xFF8F89F8)
val lightPrimary = Color(0xFF605ca8)
val buttonColor = Color(0xff6864b3)
val colorWhite = Color(0xffffffff)

// Dashboard colors
val dashboardBackgroundColor = Color(0xFF1D2123)
val accentColor = Color(0xffFACD66)
val textColor = Color(0xFFEFEEE0)
val drawerHeaderColor = Color(0xFF673AB7)
val profileImageBackgroundColor = Color.Gray

// Player colors
val playerBackgroundColor = Color(0xCC101010)
val loadingOverlayColor = Color(0x80000000)

// Browse screen colors
val selectedItemBackgroundColor = Color(0xCCFACD66)
val itemBackgroundColor = Color(0xFF33373B)

val gradientBrush = Brush.linearGradient(
    colors = listOf(lightPrimaryLighter, lightPrimary),
    start = Offset(0f, 0f),
    end = Offset(0f, Float.POSITIVE_INFINITY)
)
