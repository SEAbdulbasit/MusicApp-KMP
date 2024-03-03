package com.example.musicapp_kmp.dashboard

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.musicapp_kmp.decompose.DashboardMainComponent
import com.example.musicapp_kmp.network.models.topfiftycharts.TopFiftyCharts
import com.seiko.imageloader.rememberAsyncImagePainter


/**
 * Created by abdulbasit on 26/02/2023.
 */

@Composable
internal fun DashboardScreenLarge(
    component: DashboardMainComponent,
) {
    val state = component.viewModel.dashboardState.collectAsState()

    when (val resultedState = state.value) {
        is DashboardViewState.Failure -> Failure(resultedState.error)
        DashboardViewState.Loading -> Loading()
        is DashboardViewState.Success -> {
            DashboardViewLarge(
                resultedState
            ) { component.onOutPut(DashboardMainComponent.Output.PlaylistSelected(it)) }
        }
    }
}

@Composable
internal fun DashboardViewLarge(
    dashboardState: DashboardViewState.Success, navigateToDetails: (String) -> Unit
) {
    val listState = rememberScrollState()
    Column(
        modifier = Modifier.background(color = Color(0xFF1D2123)).fillMaxSize()
            .verticalScroll(listState).padding(bottom = 32.dp)
    ) {
        TopChartViewLarge(dashboardState.topFiftyCharts, navigateToDetails)
        FeaturedPlayLists(dashboardState.featuredPlayList, navigateToDetails)
        NewReleases(dashboardState.newReleasedAlbums, navigateToDetails)
    }
}


@Composable
internal fun TopChartViewLarge(
    topFiftyCharts: TopFiftyCharts, navigateToDetails: (String) -> Unit
) {
    Box(
        modifier = Modifier.clip(RoundedCornerShape(20.dp)).width(686.dp).height(450.dp)
            .padding(24.dp).clickable(onClick = { navigateToDetails(topFiftyCharts.id ?: "") })
    ) {
        val painter = rememberAsyncImagePainter(
            topFiftyCharts.images?.first()?.url
                ?: "https://www.linkpicture.com/q/vladimir-haltakov-PMfuunAfF2w-unsplash.jpg"
        )
        Image(
            painter,
            topFiftyCharts.images?.first()?.url
                ?: "https://www.linkpicture.com/q/vladimir-haltakov-PMfuunAfF2w-unsplash.jpg",
            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(20.dp)),
            contentScale = ContentScale.Crop
        )
        Column(modifier = Modifier.padding(16.dp).align(Alignment.BottomStart)) {
            Text(
                topFiftyCharts.name ?: "",
                style = MaterialTheme.typography.h4.copy(fontWeight = FontWeight.Bold),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                color = Color.White
            )
            Text(
                topFiftyCharts.description ?: "",
                style = MaterialTheme.typography.body2,
                color = Color.White,
                modifier = Modifier.padding(top = 6.dp)
            )
            Row(modifier = Modifier.padding(top = 40.dp)) {
                Icon(
                    imageVector = Icons.Filled.FavoriteBorder,
                    tint = Color(0xFFFACD66),
                    contentDescription = "Explore details",
                    modifier = Modifier.size(30.dp).align(Alignment.Top)
                )
                Text(
                    text = "${topFiftyCharts.followers?.total ?: 0} Likes",
                    style = MaterialTheme.typography.h5,
                    color = Color.White,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}


