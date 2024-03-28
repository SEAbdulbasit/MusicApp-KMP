import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import musicapp_kmp.CommonMainDesktop
import java.awt.Dimension
import java.awt.Toolkit

fun main() {
    application {
        val windowState = rememberWindowState(
            position = WindowPosition.Aligned(Alignment.Center),
            size = getPreferredWindowSize(800, 800)
        )

        Window(
            onCloseRequest = { this.exitApplication() },
            title = "MusicApp-KMP",
            state = windowState,
        ) {
            CommonMainDesktop()
        }
    }
}


fun getPreferredWindowSize(desiredWidth: Int, desiredHeight: Int): DpSize {
    val screenSize: Dimension = Toolkit.getDefaultToolkit().screenSize
    val preferredWidth: Int = (screenSize.width * 0.8f).toInt()
    val preferredHeight: Int = (screenSize.height * 0.8f).toInt()
    val width: Int = if (desiredWidth < preferredWidth) desiredWidth else preferredWidth
    val height: Int = if (desiredHeight < preferredHeight) desiredHeight else preferredHeight
    return DpSize(width.dp, height.dp)
}