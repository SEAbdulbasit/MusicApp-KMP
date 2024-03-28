package com.example.musicapp_kmp.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import musicapp_kmp.MainAndroid
import musicapp_kmp.di.initKoin

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        initKoin()

        setContent {
            MainAndroid()
        }
    }
}


