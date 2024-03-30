package com.example.musicapp_kmp.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.arkivanov.decompose.defaultComponentContext
import musicapp.MainAndroid
import musicapp.decompose.MusicRootImpl
import musicapp.network.SpotifyApiImpl
import musicapp.player.MediaPlayerController
import musicapp.player.PlatformContext

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val api = SpotifyApiImpl()
        val root = MusicRootImpl(
            componentContext = defaultComponentContext(),
            api = api,
            mediaPlayerController = MediaPlayerController(PlatformContext(applicationContext))
        )
        setContent {
            MainAndroid(root)
        }
    }
}
