package com.example.musicapp_kmp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color


@Composable
fun MainAndroid() {
    Column(Modifier.background(color = Color(0xFF1A1E1F))) {
        MainCommon()
    }
}