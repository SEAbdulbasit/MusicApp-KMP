package com.example.musicapp_kmp.android

import android.app.Application
import musicapp.player.PlayerServiceLocator
import musicapp.utils.PlatformContext

class MusicApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize PlayerServiceLocator with application context
        PlayerServiceLocator.init(PlatformContext(applicationContext))
    }
}