package com.example.musicapp_kmp

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.example.musicapp_kmp.chartdetails.ChartDetailsScreen
import com.example.musicapp_kmp.dashboard.DashboardScreen
import com.example.musicapp_kmp.decompose.MusicRoot
import com.example.musicapp_kmp.decompose.MusicRootImpl
import com.example.musicapp_kmp.player.MediaPlayerController

@Composable
internal fun MainCommon(mediaPlayerController: MediaPlayerController, rootComponent: MusicRootImpl) {
    MyApplicationTheme {
        Children(
            stack = rootComponent.childStack
        ) {
            when (val child = it.instance) {
                is MusicRoot.Child.Dashboard -> DashboardScreen(child.dashboardMainComponent)
                is MusicRoot.Child.Details -> ChartDetailsScreen(child.detailsComponent)
            }
        }
    }

//
//    var tracksList = remember { mutableStateOf<List<Item>>(emptyList()) }
//    val api = SpotifyApiImpl()
//    val dashboardViewModel = DashboardViewModel(api)
//
//    MyApplicationTheme {
//        Box(modifier = Modifier.fillMaxSize()) {
//            Box(modifier = Modifier.fillMaxSize()) {
//                MusicView(dashboardViewModel = dashboardViewModel,
//                    api = api,
//                    onPlayAllClicked = { tracksList.value = it })
//            }
//            Box(modifier = Modifier.align(Alignment.BottomEnd)) {
//                if (tracksList.value.isNotEmpty()) {
//                    PlayerView(tracksList.value, mediaPlayerController)
//                }
//            }
//        }
//    }
}

//@Composable
//internal fun MusicView(
//    dashboardViewModel: DashboardViewModel,
//    api: SpotifyApiImpl,
//    onPlayAllClicked: (List<Item>) -> Unit,
//) {
//    val screenNavigationState =
//        remember { mutableStateOf<SelectedScreen>(SelectedScreen.Dashboard) }
//    when (val state = screenNavigationState.value) {
//        SelectedScreen.Dashboard -> {
//            DashboardScreen(dashboardViewModel) {
//                screenNavigationState.value = SelectedScreen.PlaylistDetails(it)
//            }
//        }
//
//        is SelectedScreen.PlaylistDetails -> {
//            val chartDetailsViewModel = ChartDetailsViewModel(api, state.playlistId)
//            ChartDetailsScreen(viewModel = chartDetailsViewModel,
//                onPlayAllClicked = onPlayAllClicked,
//                onBackClicked = {
//                    screenNavigationState.value = SelectedScreen.Dashboard
//                })
//        }
//    }
//}


sealed interface SelectedScreen {
    object Dashboard : SelectedScreen
    data class PlaylistDetails(val playlistId: String) : SelectedScreen
}
