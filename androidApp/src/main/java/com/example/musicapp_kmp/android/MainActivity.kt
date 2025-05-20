package com.example.musicapp_kmp.android

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.WindowCompat
import com.arkivanov.decompose.defaultComponentContext
import musicapp.MainAndroid
import musicapp.decompose.MusicRootImpl
import musicapp.network.SpotifyApiImpl
import musicapp.player.MediaPlayerController
import musicapp.player.PlatformContext
import musicapp.player.PlayerServiceLocator

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PlayerServiceLocator.init(PlatformContext(applicationContext))
        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    1000
                )
            }
        }

        val api = SpotifyApiImpl()
        val root = MusicRootImpl(
            componentContext = defaultComponentContext(),
            api = api,
            mediaPlayerController = PlayerServiceLocator.playerController
        )
        setContent {
            MainAndroid(root)
        }
    }
}
