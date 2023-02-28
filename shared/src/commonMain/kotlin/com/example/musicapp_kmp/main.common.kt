package com.example.musicapp_kmp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.musicapp_kmp.chartdetails.ChartDetailsScreen
import com.example.musicapp_kmp.chartdetails.ChartDetailsViewModel
import com.example.musicapp_kmp.dashboard.DashboardScreen
import com.example.musicapp_kmp.dashboard.DashboardViewModel
import com.example.musicapp_kmp.network.SpotifyApiImpl

@Composable
internal fun MainCommon() {
    MyApplicationTheme {
        val screenNavigationState = remember { mutableStateOf<SelectedScreen>(SelectedScreen.Dashboard) }

        val api = SpotifyApiImpl()
        val dashboardViewModel = DashboardViewModel(api)

        when (val screen = screenNavigationState.value) {
            SelectedScreen.Dashboard -> {
                DashboardScreen(dashboardViewModel) {
                    screenNavigationState.value = SelectedScreen.PlaylistDetails(it)
                }
            }

            is SelectedScreen.PlaylistDetails -> {
                val chartDetailsViewModel = ChartDetailsViewModel(api, screen.playlistId)
                ChartDetailsScreen(chartDetailsViewModel)
            }
        }
    }
}


sealed interface SelectedScreen {
    object Dashboard : SelectedScreen
    data class PlaylistDetails(val playlistId: String) : SelectedScreen
}