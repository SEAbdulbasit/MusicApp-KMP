package com.example.musicapp_kmp.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.arkivanov.decompose.defaultComponentContext
import musicapp_kmp.MainAndroid
import musicapp_kmp.decompose.MusicRootImpl
import musicapp_kmp.di.initKoin
import musicapp_kmp.network.SpotifyApiImpl
import musicapp_kmp.player.MediaPlayerController
import musicapp_kmp.player.PlatformContext
import org.koin.android.ext.koin.androidContext

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        initKoin {
            androidContext(this@MainActivity)
        }

        val root = MusicRootImpl(
            componentContext = defaultComponentContext(),
            api = SpotifyApiImpl(),
            mediaPlayerController = MediaPlayerController(PlatformContext(applicationContext))
        )
        setContent {
            MainAndroid(root)
        }
    }
}


