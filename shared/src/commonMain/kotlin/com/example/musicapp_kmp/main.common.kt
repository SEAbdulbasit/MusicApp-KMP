package com.example.musicapp_kmp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.musicapp_kmp.chartdetails.ChartDetailsScreen
import com.example.musicapp_kmp.chartdetails.ChartDetailsViewModel
import com.example.musicapp_kmp.dashboard.DashboardScreen
import com.example.musicapp_kmp.dashboard.DashboardViewModel
import com.example.musicapp_kmp.network.SpotifyApiImpl
import com.example.musicapp_kmp.network.models.topfiftycharts.Item
import com.example.musicapp_kmp.player.MediaPlayerController
import com.example.musicapp_kmp.playerview.PlayerView

@Composable
internal fun MainCommon(mediaPlayerController: MediaPlayerController) {
    var tracksList = remember { mutableStateOf<List<Item>>(emptyList()) }
    val api = SpotifyApiImpl()
    val dashboardViewModel = DashboardViewModel(api)

    MyApplicationTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize()) {
                MusicView(dashboardViewModel = dashboardViewModel,
                    api = api,
                    onPlayAllClicked = { tracksList.value = it })
            }
            Box(modifier = Modifier.align(Alignment.BottomEnd)) {
                if (tracksList.value.isNotEmpty()) {
                    PlayerView(tracksList.value, mediaPlayerController)
                }
            }
        }
    }
}

@Composable
internal fun MusicView(
    dashboardViewModel: DashboardViewModel,
    api: SpotifyApiImpl,
    onPlayAllClicked: (List<Item>) -> Unit,
) {
    val screenNavigationState =
        remember { mutableStateOf<SelectedScreen>(SelectedScreen.Dashboard) }
    when (val state = screenNavigationState.value) {
        SelectedScreen.Dashboard -> {
            DashboardScreen(dashboardViewModel) {
                screenNavigationState.value = SelectedScreen.PlaylistDetails(it)
            }
        }

        is SelectedScreen.PlaylistDetails -> {
            val chartDetailsViewModel = ChartDetailsViewModel(api, state.playlistId)
            ChartDetailsScreen(viewModel = chartDetailsViewModel,
                onPlayAllClicked = onPlayAllClicked,
                onBackClicked = {
                    screenNavigationState.value = SelectedScreen.Dashboard
                })
        }
    }
}


sealed interface SelectedScreen {
    object Dashboard : SelectedScreen
    data class PlaylistDetails(val playlistId: String) : SelectedScreen
}
