package musicapp_kmp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import musicapp_kmp.chartdetails.ChartDetailsScreen
import musicapp_kmp.chartdetails.ChartDetailsViewModel
import musicapp_kmp.dashboard.DashboardScreen
import musicapp_kmp.decompose.ChartDetailsComponent
import musicapp_kmp.decompose.MusicRootImpl
import musicapp_kmp.decompose.PlayerComponent
import musicapp_kmp.network.models.topfiftycharts.Item
import musicapp_kmp.playerview.PlayerView
import musicapp_kmp.playerview.PlayerViewModel
import org.koin.compose.koinInject

//@Composable
//internal fun MainCommon(
//    rootComponent: MusicRootImpl,
//    isLargeScreen: Boolean
//) {
//
//    val dialogOverlay by rootComponent.dialogOverlay.subscribeAsState()
//
//    MyApplicationTheme {
//        Box(modifier = Modifier.fillMaxSize()) {
//            Box(modifier = Modifier.fillMaxSize()) {
//                Children(
//                    stack = rootComponent.childStack
//                ) {
//                    when (val child = it.instance) {
//                        is MusicRoot.Child.Dashboard -> {
//                            if (isLargeScreen)
//                                DashboardScreenLarge(child.dashboardMainComponent)
//                            else
//                                DashboardScreen(child.dashboardMainComponent)
//                        }
//
//                        is MusicRoot.Child.Details -> {
//                            if (isLargeScreen)
//                                ChartDetailsScreenLarge(child.detailsComponent)
//                            else
//                                ChartDetailsScreen(child.detailsComponent)
//                        }
//                    }
//                }
//            }
//            Box(modifier = Modifier.align(Alignment.BottomEnd)) {
//                dialogOverlay.overlay?.instance?.also {
//                    PlayerView(it)
//                }
//            }
//        }
//    }
//}

@Composable
internal fun MainCommon(
    rootComponent: MusicRootImpl, isLargeScreen: Boolean
) {

    val screen = remember { mutableStateOf<Screens>(Screens.Dashboard) }
    val chartDetailsFlow = MutableSharedFlow<ChartDetailsComponent.Input>()
    val playerInput = MutableSharedFlow<PlayerComponent.Input>()
    val playingTrackId = remember { mutableStateOf<String>("") }
    val playList = remember { mutableStateOf<List<Item>>(emptyList()) }
    val dialogOverlay by rootComponent.dialogOverlay.subscribeAsState()
    val scope = rememberCoroutineScope()

    MyApplicationTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize()) {
                when (val value = screen.value) {
                    is Screens.ChartDetails -> {
                        ChartDetailsScreen(chartDetailsViewModel = ChartDetailsViewModel(
                            chartDetailsFlow = chartDetailsFlow,
                            api = koinInject(),
                            playingTrackId = playingTrackId.value,
                            playlistId = value.playListId
                        ), onPlayAllClicked = {
                            playList.value = it
                        }, onPlayTrack = {

                        }, onGoBack = {
                            screen.value = Screens.Dashboard
                        })
                    }

                    Screens.Dashboard -> DashboardScreen(onPlayListSelected = {
                        screen.value = Screens.ChartDetails(it)
                    })
                }
                if (playList.value.isNotEmpty()) {

                    PlayerView(
                        PlayerViewModel.getViewModel(
                            mediaPlayerController = koinInject(),
                            trackList = playList.value,
                            playerInputs = playerInput,
                        ),
                        onTrackUpdated = {
                            scope.launch {
                                chartDetailsFlow.emit(ChartDetailsComponent.Input.TrackUpdated(it))
                            }
                        }
                    )
                }
            }
//            Box(modifier = Modifier.align(Alignment.BottomEnd)) {
//                if (playList.value.isNotEmpty()) {
//
//                }
//            }
        }
    }
}


sealed interface Screens {
    data object Dashboard : Screens
    data class ChartDetails(val playListId: String) : Screens
}

