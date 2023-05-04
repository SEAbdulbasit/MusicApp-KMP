package com.example.musicapp_kmp.dashboard

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
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
import com.example.musicapp_kmp.network.models.featuredplaylist.FeaturedPlayList
import com.example.musicapp_kmp.network.models.newreleases.NewReleasedAlbums
import com.example.musicapp_kmp.network.models.topfiftycharts.TopFiftyCharts
import com.seiko.imageloader.rememberAsyncImagePainter


/**
 * Created by abdulbasit on 26/02/2023.
 */

@Composable
internal fun DashboardScreen(dashboardMainComponent: DashboardMainComponent) {
    val state = dashboardMainComponent.viewModel.dashboardState.collectAsState()

    when (val resultedState = state.value) {
        is DashboardViewState.Failure -> Failure(resultedState.error)
        DashboardViewState.Loading -> Loading()
        is DashboardViewState.Success -> {
            DashboardView(resultedState) {
                dashboardMainComponent.onOutPut(DashboardMainComponent.Output.PlaylistSelected(it))
            }
        }
    }
}

@Composable
internal fun Loading() {
    Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center),
            color = Color(0xFFFACD66),
        )
    }
}

@Composable
internal fun Failure(message: String) {
    Box(modifier = Modifier.fillMaxSize().padding(32.dp)) {
        Text(
            text = message,
            modifier = Modifier.align(Alignment.Center),
            style = MaterialTheme.typography.body1.copy(color = Color(0xFFFACD66))
        )
    }
}

@Composable
internal fun DashboardView(
    dashboardState: DashboardViewState.Success,
    navigateToDetails: (String) -> Unit
) {
    val listState = rememberScrollState()
    Column(
        modifier = Modifier.background(color = Color(0xFF1D2123)).fillMaxSize()
            .verticalScroll(listState)
            .padding(bottom = 32.dp)
    ) {
        TopChartView(dashboardState.topFiftyCharts, navigateToDetails)
        FeaturedPlayLists(dashboardState.featuredPlayList, navigateToDetails)
        NewReleases(dashboardState.newReleasedAlbums, navigateToDetails)
    }
}

@Composable
internal fun TopChartView(topFiftyCharts: TopFiftyCharts, navigateToDetails: (String) -> Unit) {
    Box(
        modifier = Modifier.aspectRatio(ratio = (367.0 / 450.0).toFloat())
            .clip(RoundedCornerShape(20.dp))
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

@Composable
internal fun FeaturedPlayLists(
    featuredPlayList: FeaturedPlayList,
    navigateToDetails: (String) -> Unit
) {
    Column(modifier = Modifier.padding(top = 46.dp)) {
        Text(
            "Featured Playlist",
            style = MaterialTheme.typography.h6.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFFEFEEE0)
            ),
            modifier = Modifier.padding(start = 16.dp)
        )
        val listState = rememberLazyListState()

        LazyRow(
            modifier = Modifier.padding(top = 16.dp).fillMaxSize(),
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(items = featuredPlayList.playlists?.items ?: emptyList()) { playList ->
                Box(
                    modifier = Modifier.width(232.dp).clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF1A1E1F))
                        .clickable(onClick = { navigateToDetails(playList.id ?: "") })
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        val painter = rememberAsyncImagePainter(
                            playList.images?.first()?.url
                                ?: "https://www.linkpicture.com/q/vladimir-haltakov-PMfuunAfF2w-unsplash.jpg"
                        )
                        Image(
                            painter,
                            playList.images?.first()?.url
                                ?: "https://www.linkpicture.com/q/vladimir-haltakov-PMfuunAfF2w-unsplash.jpg",
                            modifier = Modifier.clip(RoundedCornerShape(20.dp)).width(100.dp)
                                .height(100.dp),
                            contentScale = ContentScale.Crop
                        )
                        Text(
                            text = playList.name ?: "",
                            style = MaterialTheme.typography.body1.copy(color = Color.White),
                            modifier = Modifier.padding(top = 16.dp),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                        Text(
                            text = playList.description ?: "",
                            style = MaterialTheme.typography.caption.copy(
                                color = Color.White.copy(
                                    alpha = 0.5f
                                )
                            ),
                            modifier = Modifier.padding(top = 8.dp),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                        Text(
                            text = "${(playList.tracks?.total ?: 0)} tracks",
                            style = MaterialTheme.typography.body2.copy(color = Color.White),
                            modifier = Modifier.padding(top = 24.dp)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        tint = Color(0xFFFACD66),
                        contentDescription = "Favorite",
                        modifier = Modifier.padding(top = 16.dp, end = 16.dp).size(30.dp)
                            .align(Alignment.TopEnd)
                    )
                }
            }
        }
    }
}

@Composable
internal fun NewReleases(
    newReleasedAlbums: NewReleasedAlbums,
    navigateToDetails: (String) -> Unit
) {
    Column(modifier = Modifier.padding(top = 46.dp).fillMaxWidth()) {
        Text(
            "New releases",
            style = MaterialTheme.typography.h6.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFFEFEEE0)
            ),
            modifier = Modifier.padding(start = 16.dp)
        )
        val listState = rememberLazyListState()

        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp)
        ) {
            items(items = newReleasedAlbums.albums?.items ?: emptyList()) { album ->
                Box(Modifier.width(153.dp)) {
                    Column {
                        val painter = rememberAsyncImagePainter(
                            album.images?.first()?.url
                                ?: "https://www.linkpicture.com/q/vladimir-haltakov-PMfuunAfF2w-unsplash.jpg"
                        )
                        Image(
                            painter,
                            album.images?.first()?.url
                                ?: "https://www.linkpicture.com/q/vladimir-haltakov-PMfuunAfF2w-unsplash.jpg",
                            modifier = Modifier.width(153.dp).height(153.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .clickable(onClick = { navigateToDetails(album.id ?: "") }),
                            contentScale = ContentScale.Crop
                        )
                        Text(
                            text = album.name ?: "",
                            style = MaterialTheme.typography.caption.copy(color = Color.White),
                            modifier = Modifier.padding(top = 16.dp),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                        Text(
                            text = "${(album.totalTracks ?: 0)} tracks",
                            style = MaterialTheme.typography.caption.copy(
                                color = Color.White.copy(
                                    alpha = 0.5f
                                )
                            ),
                            modifier = Modifier.padding(top = 8.dp),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
