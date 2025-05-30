package com.example.musicapp_kmp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.seiko.imageloader.Bitmap
import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.LocalImageLoader
import com.seiko.imageloader.cache.memory.MemoryCacheBuilder
import com.seiko.imageloader.cache.memory.MemoryKey
import com.seiko.imageloader.cache.memory.maxSizePercent
import com.seiko.imageloader.component.setupDefaultComponents
import com.seiko.imageloader.intercept.bitmapMemoryCacheConfig
import musicapp.MainCommon
import musicapp.decompose.MusicRootImpl
import java.lang.System.identityHashCode


@Composable
fun MainAndroid(root: MusicRootImpl) {
    Column(Modifier.background(color = Color(0xFF1A1E1F))) {
        val context = LocalContext.current
        CompositionLocalProvider(
            LocalImageLoader provides ImageLoader {
                components {
                    setupDefaultComponents(context)
                }
                interceptor {
                    bitmapMemoryCacheConfig(
                        valueHashProvider = { identityHashCode(it) },
                        valueSizeProvider = { 500 },
                        block = fun MemoryCacheBuilder<MemoryKey, Bitmap>.() {
                            maxSizePercent(context.applicationContext)
                        }
                    )
                }
            },
        ) {
            MainCommon(root, false)
        }
    }
}

