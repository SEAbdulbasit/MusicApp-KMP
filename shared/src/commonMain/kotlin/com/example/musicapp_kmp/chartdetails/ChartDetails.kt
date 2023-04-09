package com.example.musicapp_kmp.chartdetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.musicapp_kmp.decompose.ChartDetailsComponent
import com.example.musicapp_kmp.network.models.topfiftycharts.Item
import com.example.musicapp_kmp.network.models.topfiftycharts.TopFiftyCharts
import com.seiko.imageloader.rememberAsyncImagePainter


/**
 * Created by abdulbasit on 28/02/2023.
 */
@Composable
internal fun ChartDetailsScreen(
    chartDetailsComponent: ChartDetailsComponent,
) {

    val state = chartDetailsComponent.viewModel.chartDetailsViewState.collectAsState()
    when (val resultedState = state.value) {
        is ChartDetailsViewState.Failure -> Failure(resultedState.error)
        ChartDetailsViewState.Loading -> Loading()
        is ChartDetailsViewState.Success ->
            ChartDetailsView(
                chartDetails = resultedState.chartDetails,
                playingTrackId = resultedState.playingTrackId,
                onPlayAllClicked = { chartDetailsComponent.onOutPut(ChartDetailsComponent.Output.OnPlayAllSelected(it)) },
                onPlayTrack = { chartDetailsComponent.onOutPut(ChartDetailsComponent.Output.OnTrackSelected(it)) }
            )
    }
    Icon(
        imageVector = Icons.Filled.ArrowBack,
        tint = Color(0xFFFACD66),
        contentDescription = "Forward",
        modifier = Modifier.padding(all = 16.dp).size(32.dp).clickable(onClick = {
            chartDetailsComponent.onOutPut(ChartDetailsComponent.Output.GoBack)
        })
    )
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
    Box(modifier = Modifier.fillMaxSize()) {
        Text(text = message, modifier = Modifier.align(Alignment.Center))
    }
}


@Composable
internal fun ChartDetailsView(
    chartDetails: TopFiftyCharts,
    onPlayAllClicked: (List<Item>) -> Unit,
    onPlayTrack: (String) -> Unit,
    playingTrackId: Any
) {

    val selectedTrack = remember { mutableStateOf(playingTrackId) }
    val painter = rememberAsyncImagePainter(chartDetails.images?.first()?.url.orEmpty())

    LaunchedEffect(playingTrackId) {
        selectedTrack.value = playingTrackId
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painter,
            contentDescription = chartDetails.images?.first()?.url.orEmpty(),
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier.fillMaxSize().background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xCC1D2123), Color(0xFF1D2123)
                    )
                )
            )
        )

        LazyColumn(
            modifier = Modifier.padding(horizontal = 30.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                Image(
                    painter = painter,
                    contentDescription = chartDetails.images?.first()?.url.orEmpty(),
                    modifier = Modifier.padding(top = 24.dp, bottom = 24.dp).fillMaxWidth().aspectRatio(1f)
                        .clip(RoundedCornerShape(25.dp)),
                    contentScale = ContentScale.Crop,
                )
                Text(
                    text = chartDetails.name ?: "", style = MaterialTheme.typography.h4.copy(color = Color(0XFFA4C7C6))
                )
                Text(
                    text = chartDetails.description.orEmpty(),
                    style = MaterialTheme.typography.body2.copy(color = Color(0XFFEFEEE0)),
                    modifier = Modifier.padding(top = 8.dp)
                )
                Text(
                    text = "${chartDetails.tracks?.items?.size ?: 0} songs",
                    style = MaterialTheme.typography.body2.copy(color = Color(0XFFEFEEE0)),
                    modifier = Modifier.padding(top = 8.dp)
                )
                Spacer(Modifier.height(32.dp).fillMaxWidth())
                OptionChips(onPlayAllClicked, chartDetails.tracks?.items ?: emptyList())
            }
            items(chartDetails.tracks?.items ?: emptyList()) { track ->
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(20.dp)).fillMaxWidth().background(
                        if (track.track?.id.orEmpty() == selectedTrack.value) Color(0xCCFACD66)
                        else Color(0xFF33373B)
                    ).padding(16.dp)
                ) {
                    var active by remember { mutableStateOf(false) }
                    Row(modifier = Modifier.fillMaxWidth()) {
                        val painter = rememberAsyncImagePainter(
                            track.track?.album?.images?.first()?.url.orEmpty()
                        )
                        Box(modifier = Modifier
                            .clickable {
                                onPlayTrack(track.track?.id.orEmpty())
                            }) {
                            Image(
                                painter,
                                track.track?.album?.images?.first()?.url.orEmpty(),
                                modifier = Modifier.clip(RoundedCornerShape(5.dp)).width(40.dp).height(40.dp)
                                    .pointerInput(track) {
                                        while (true) {
                                            val event = awaitPointerEventScope { awaitPointerEvent() }
                                            when (event.type) {
                                                // androidx.compose.ui.input.pointer.PointerEventType.Enter -> active = true
                                                // androidx.compose.ui.input.pointer.PointerEventType.Exit -> active = false
                                            }
                                        }
                                    },
                                contentScale = ContentScale.Crop
                            )
                            if (active) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    tint = Color(0xFFFACD66),
                                    contentDescription = "Play All",
                                    modifier = Modifier.size(40.dp)
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(Color.Black.copy(alpha = 0.7f))
                                )
                            }
                        }
                        Column(Modifier.weight(1f).padding(start = 8.dp).align(Alignment.Top)) {
                            Text(
                                text = track.track?.name.orEmpty(), style = MaterialTheme.typography.caption.copy(
                                    color = Color(
                                        0XFFEFEEE0
                                    )
                                )
                            )
                            Text(
                                text = track.track?.artists?.map { it.name }?.joinToString(",").orEmpty(),
                                style = MaterialTheme.typography.caption.copy(
                                    color = Color(
                                        0XFFEFEEE0
                                    )
                                ),
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                        Text(
                            text = "${(((track.track?.durationMs ?: 0) / (1000 * 60)) % 60)}:${(((track.track?.durationMs ?: 0) / (1000)) % 60)}",
                            style = MaterialTheme.typography.caption.copy(color = Color(0XFFEFEEE0)),
                            modifier = Modifier.align(
                                Alignment.Bottom
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun OptionChips(onPlayAllClicked: (List<Item>) -> Unit, items: List<Item>) {
    Box(
        modifier = Modifier.clip(RoundedCornerShape(32.dp)).background(Color(0xFF33373B))
            .clickable(onClick = { onPlayAllClicked(items) }).padding(
                start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp
            )
    ) {
        Row {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                tint = Color(0xFFFACD66),
                contentDescription = "Play All",
                modifier = Modifier.padding(end = 8.dp).size(16.dp).align(Alignment.CenterVertically)
            )
            Text(
                text = "Play All", style = MaterialTheme.typography.caption.copy(color = Color(0XFFEFEEE0))
            )
        }
    }
}