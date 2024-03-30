package musicapp_kmp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import musicapp_kmp.chartdetails.ChartDetailsScreenLarge
import musicapp_kmp.dashboard.DashboardScreen
import musicapp_kmp.dashboard.DashboardScreenLarge
import musicapp_kmp.decompose.MusicRoot
import musicapp_kmp.playerview.PlayerView
import musicapp_kmp.chartdetails.ChartDetailsScreen

@Composable
internal fun MainCommon(
    rootComponent: MusicRoot,
    isLargeScreen: Boolean
) {
    val dialogOverlay by rootComponent.dialogOverlay.subscribeAsState()

    MyApplicationTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize()) {
                Children(
                    stack = rootComponent.childStack
                ) {
                    when (val child = it.instance) {
                        is MusicRoot.Child.Dashboard -> {
                            if (isLargeScreen)
                                DashboardScreenLarge(child.dashboardMainComponent)
                            else
                                DashboardScreen(child.dashboardMainComponent)
                        }

                        is MusicRoot.Child.Details -> {
                            if (isLargeScreen)
                                ChartDetailsScreenLarge(child.detailsComponent)
                            else
                                ChartDetailsScreen(child.detailsComponent)
                        }
                    }
                }
            }
            Box(modifier = Modifier.align(Alignment.BottomEnd)) {
                dialogOverlay.child?.instance?.also {
                    PlayerView(it)
                }
            }
        }
    }
}
