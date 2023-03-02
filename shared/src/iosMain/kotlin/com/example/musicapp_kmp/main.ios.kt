package com.example.musicapp_kmp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Application
import platform.UIKit.UIViewController

fun MainiOS(): UIViewController = Application("Music-App") {
    Column(Modifier.background(color = Color(0xFF1A1E1F))) {
        Box(
            modifier = Modifier.fillMaxWidth()
                .height(40.dp).background(color = Color(0xFF1A1E1F))
        )
        MainCommon()
    }
}