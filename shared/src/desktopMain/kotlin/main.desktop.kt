package musicapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.seiko.imageloader.Bitmap
import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.LocalImageLoader
import com.seiko.imageloader.cache.memory.MemoryCacheBuilder
import com.seiko.imageloader.cache.memory.MemoryKey
import com.seiko.imageloader.cache.memory.maxSizePercent
import com.seiko.imageloader.component.setupDefaultComponents
import com.seiko.imageloader.intercept.bitmapMemoryCacheConfig
import com.seiko.imageloader.util.identityHashCode
import musicapp.decompose.MusicRoot

@Composable
fun CommonMainDesktop(rootComponent: MusicRoot) {
    Box(Modifier.background(color = Color(0xFF1A1E1F)).fillMaxSize()) {
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
            MainCommon(rootComponent, true)
        }
    }
}