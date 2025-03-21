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
import com.seiko.imageloader.Bitmap
import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.LocalImageLoader
import com.seiko.imageloader.cache.memory.MemoryCacheBuilder
import com.seiko.imageloader.cache.memory.MemoryKey
import com.seiko.imageloader.cache.memory.maxSizePercent
import com.seiko.imageloader.component.setupDefaultComponents
import com.seiko.imageloader.intercept.bitmapMemoryCacheConfig
import com.seiko.imageloader.util.identityHashCode
import musicapp.decompose.MusicRootImpl
import musicapp.network.SpotifyApiImpl
import musicapp.player.MediaPlayerController
import musicapp.player.PlatformContext
import platform.UIKit.UIViewController

fun MainiOS(
    lifecycle: LifecycleRegistry,
): UIViewController = ComposeUIViewController(configure = { enforceStrictPlistSanityCheck = false }) {
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
                components {
                    setupDefaultComponents()
                }
                interceptor {
                    bitmapMemoryCacheConfig(
                        valueHashProvider = { identityHashCode(it) },
                        valueSizeProvider = { 500 },
                        block = fun MemoryCacheBuilder<MemoryKey, Bitmap>.() {
                            maxSizePercent(0.25)
                        }
                    )
                }
            },
        ) {
            MainCommon(rootComponent, false)
        }
    }
}
