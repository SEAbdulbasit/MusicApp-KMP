package com.example.musicapp_kmp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
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
    // Get the system bars insets and apply padding to avoid content being hidden behind the navigation bar
    val systemBarsInsets = WindowInsets.systemBars.only(WindowInsetsSides.Bottom).asPaddingValues()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF1A1E1F))
            .padding(bottom = systemBarsInsets.calculateBottomPadding())
    ) {
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
