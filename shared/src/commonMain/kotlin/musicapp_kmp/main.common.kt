package musicapp_kmp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import musicapp_kmp.dashboard.DashboardScreen

@Composable
internal fun MainCommon(
    isLargeScreen: Boolean
) {

    MyApplicationTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            DashboardScreen()
        }
    }

//        MyApplicationTheme {
//            Box(modifier = Modifier.fillMaxSize()) {
//                Box(modifier = Modifier.fillMaxSize()) {
//                    Children(
//                        stack = rootComponent.childStack
//                    ) {
//                        when (val child = it.instance) {
//                            is MusicRoot.Child.Dashboard -> {
//                                if (isLargeScreen)
//                                    DashboardScreenLarge(child.dashboardMainComponent)
//                                else
//                                    DashboardScreen(child.dashboardMainComponent)
//                            }
//
//                            is MusicRoot.Child.Details -> {
//                                if (isLargeScreen)
//                                    ChartDetailsScreenLarge(child.detailsComponent)
//                                else
//                                    ChartDetailsScreen(child.detailsComponent)
//                            }
//                        }
//                    }
//                }
//                Box(modifier = Modifier.align(Alignment.BottomEnd)) {
//                    dialogOverlay.overlay?.instance?.also {
//                        PlayerView(it)
//                    }
//                }
//            }
//        }
}
