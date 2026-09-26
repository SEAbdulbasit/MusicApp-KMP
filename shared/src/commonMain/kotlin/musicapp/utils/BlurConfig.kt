package musicapp.utils

import androidx.compose.ui.graphics.blur.BlurStop
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Platform-calibrated blur configuration.
 *
 * Android uses hardware RenderEffect which is naturally very dense and pronounced.
 * iOS, Desktop, and Web use Skia / Skiko shaders which require a higher radius
 * and fraction to achieve matching perceived intensity and clear the top safe area / notch.
 */
object AppBlurConfig {
    // Top progressive blur radius for list headers
    val headerBlurRadius: Dp
        get() = if (isAndroidPlatform) 20.dp else 36.dp

    // Top progressive blur stop fraction for list headers
    val headerBlurFraction: Float
        get() = if (isAndroidPlatform) 0.08f else 0.20f

    // Header blur stops for progressive header blur
    val headerBlurStops: List<BlurStop>
        get() = listOf(
            BlurStop(fraction = 0.0f, radius = headerBlurRadius),
            BlurStop(fraction = headerBlurFraction, radius = 0.dp),
            BlurStop(fraction = 1.0f, radius = 0.dp)
        )

    // Image card blur (TopChartView, ChartDetails background)
    val imageCardBlurRadius: Dp
        get() = if (isAndroidPlatform) 20.dp else 36.dp

    // Top header backdrop overlay blur radius (ChartDetails back bar)
    val topBarBackdropBlurRadius: Dp
        get() = if (isAndroidPlatform) 24.dp else 40.dp

    // Ambient glow blur in full-screen player
    val ambientGlowBlurRadius: Dp
        get() = if (isAndroidPlatform) 28.dp else 44.dp
}
