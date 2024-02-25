import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.example.musicapp_kmp.decompose.MusicRootImpl
import com.example.musicapp_kmp.network.SpotifyApiImpl
import com.example.musicapp_kmp.player.MediaPlayerController
import org.jetbrains.skiko.wasm.onWasmReady

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    onWasmReady {
        val lifecycle = LifecycleRegistry()
        val rootComponent =
            MusicRootImpl(
                componentContext = DefaultComponentContext(
                    lifecycle = lifecycle,
                ), api = SpotifyApiImpl(), mediaPlayerController = MediaPlayerController()
            )

        lifecycle.resume()
        CanvasBasedWindow("MusicApp-KMP") {
            CommonMainWeb(rootComponent)
        }
    }
}



