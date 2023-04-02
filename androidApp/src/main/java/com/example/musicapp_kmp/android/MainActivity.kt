package com.example.musicapp_kmp.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.arkivanov.decompose.defaultComponentContext
import com.example.musicapp_kmp.MainAndroid
import com.example.musicapp_kmp.decompose.MusicRootImpl
import com.example.musicapp_kmp.network.SpotifyApiImpl
import com.example.musicapp_kmp.player.MediaPlayerController

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val api = SpotifyApiImpl()
        val root = MusicRootImpl(
            componentContext = defaultComponentContext(),
            api = api,
            mediaPlayerController = MediaPlayerController()
        )
        setContent {
            MainAndroid(root)
        }
    }
}
