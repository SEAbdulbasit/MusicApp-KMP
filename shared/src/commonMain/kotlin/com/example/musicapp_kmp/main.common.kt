package com.example.musicapp_kmp

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.musicapp_kmp.dashboard.DashboardScreen
import com.example.musicapp_kmp.dashboard.DashboardViewModel

@Composable
internal fun MainCommon() {
    MyApplicationTheme {
        val viewModel = DashboardViewModel()
        DashboardScreen(viewModel)
    }
}