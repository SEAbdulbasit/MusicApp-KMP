import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.LocalImageLoader
import com.seiko.imageloader.cache.memory.maxSizePercent
import com.seiko.imageloader.component.setupDefaultComponents
import musicapp_kmp.MainCommon
import musicapp_kmp.decompose.MusicRoot


@Composable
fun CommonMainWeb(root: MusicRoot) {
    CompositionLocalProvider(
        LocalImageLoader provides ImageLoader {
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
        Box(Modifier.background(color = Color(0xFF1A1E1F)).fillMaxSize()) {
            MainCommon(root, true)
        }
    }
}


