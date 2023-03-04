package com.example.travelapp_kmp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.musicapp_kmp.MainCommonLarge
import com.example.musicapp_kmp.player.MediaPlayerController

@Composable
fun CommonMainDesktop() {
    Box(Modifier.background(color = Color(0xFF1A1E1F)).fillMaxSize()) {
        val mediaPlayer = MediaPlayerController()
        MainCommonLarge(mediaPlayer)
    }
}