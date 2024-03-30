package musicapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.LocalImageLoader
import com.seiko.imageloader.cache.memory.maxSizePercent
import com.seiko.imageloader.component.setupDefaultComponents
import com.seiko.imageloader.util.DebugLogger
import com.seiko.imageloader.util.LogPriority
import musicapp.decompose.MusicRootImpl
import musicapp.network.SpotifyApiImpl
import musicapp.player.MediaPlayerController
import musicapp.player.PlatformContext
import platform.UIKit.UIViewController

fun MainiOS(
    lifecycle: LifecycleRegistry,
): UIViewController = ComposeUIViewController {
    val rootComponent = MusicRootImpl(
        componentContext = DefaultComponentContext(lifecycle = lifecycle),
        api = SpotifyApiImpl(),
        mediaPlayerController = MediaPlayerController(PlatformContext())
    )

    Column(Modifier.background(color = Color(0xFF1A1E1F))) {
        Box(
            modifier = Modifier.fillMaxWidth().height(40.dp).background(color = Color(0xFF1A1E1F))
        )
        CompositionLocalProvider(
            LocalImageLoader provides ImageLoader {
                logger = DebugLogger(LogPriority.VERBOSE)
                components {
                    setupDefaultComponents(imageScope)
                }
                interceptor {
                    memoryCacheConfig {
                        maxSizePercent(0.25)
                    }
                }
            },
        ) {
            MainCommon(rootComponent, false)
        }
    }
}
