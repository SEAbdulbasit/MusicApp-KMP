package com.example.travelapp_kmp

import androidx.compose.runtime.Composable
import com.example.musicapp_kmp.MainCommon
import com.example.musicapp_kmp.MainCommonLarge
import com.example.musicapp_kmp.player.MediaPlayerController

@Composable
fun CommonMainDesktop() {
    MainCommonLarge(MediaPlayerController())
}