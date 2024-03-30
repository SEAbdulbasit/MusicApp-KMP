package musicapp.chartdetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import musicapp.decompose.ChartDetailsComponent
import musicapp.network.models.topfiftycharts.Item
import musicapp.network.models.topfiftycharts.TopFiftyCharts
import com.seiko.imageloader.rememberAsyncImagePainter


/**
 * Created by abdulbasit on 28/02/2023.
 */
@Composable
internal fun ChartDetailsScreenLarge(
    chartDetailsComponent: ChartDetailsComponent,
) {
    val state = chartDetailsComponent.viewModel.chartDetailsViewState.collectAsState()

    when (val resultedState = state.value) {
        is ChartDetailsViewState.Failure -> Failure(resultedState.error)
        ChartDetailsViewState.Loading -> Loading()
        is ChartDetailsViewState.Success -> ChartDetailsViewLarge(
            chartDetails = resultedState.chartDetails,
            playingTrackId = resultedState.playingTrackId,
            onPlayAllClicked = { chartDetailsComponent.onOutPut(ChartDetailsComponent.Output.OnPlayAllSelected(it)) },
            onPlayTrack = { id, list ->
                chartDetailsComponent.onOutPut(ChartDetailsComponent.Output.OnTrackSelected(id, list))
            }
        )
    }
    IconButton(onClick = { chartDetailsComponent.onOutPut(ChartDetailsComponent.Output.GoBack) }) {
        Icon(
            Icons.Filled.ArrowBack,
            contentDescription = "Forward",
            tint = Color(0xFFFACD66),
            modifier = Modifier.padding(all = 16.dp).size(32.dp)
        )
    }

    /*Icon(
        imageVector = Icons.Filled.ArrowBack,
        tint = Color(0xFFFACD66),
        contentDescription = "Forward",
        modifier = Modifier.padding(all = 8.dp).size(32.dp).clickable(onClick = {
            chartDetailsComponent.onOutPut(ChartDetailsComponent.Output.GoBack)
        })
    )*/
}

@Composable
internal fun ChartDetailsViewLarge(
    chartDetails: TopFiftyCharts,
    onPlayAllClicked: (List<Item>) -> Unit,
    onPlayTrack: (String, List<Item>) -> Unit,
    playingTrackId: String
) {
    val painter = rememberAsyncImagePainter(chartDetails.images?.first()?.url.orEmpty())
    val selectedTrack = remember { mutableStateOf(playingTrackId) }

    LaunchedEffect(playingTrackId) {
        selectedTrack.value = playingTrackId
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter,
            chartDetails.images?.first()?.url.orEmpty(),
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
    }

    LazyColumn(
        modifier = Modifier.padding(horizontal = 63.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {

        item {
            Box(modifier = Modifier.fillMaxSize()) {
                Row(modifier = Modifier.padding(16.dp).align(Alignment.TopCenter)) {
                    Image(
                        painter = painter,
                        contentDescription = chartDetails.images?.first()?.url.orEmpty(),
                        modifier = Modifier.padding(top = 24.dp, bottom = 20.dp).height(284.dp)
                            .width(284.dp)
                            .aspectRatio(1f).clip(RoundedCornerShape(25.dp)),
                        contentScale = ContentScale.Crop,
                    )
                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.align(Alignment.Bottom).padding(16.dp)
                    ) {
                        Text(
                            text = chartDetails.name.orEmpty(),
                            style = MaterialTheme.typography.h4.copy(color = Color(0XFFA4C7C6))
                        )
                        Text(
                            text = chartDetails.description.orEmpty(),
                            style = MaterialTheme.typography.body2.copy(color = Color(0XFFEFEEE0)),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Text(
                            text = "${chartDetails.tracks?.items?.size ?: 0} songs",
                            style = MaterialTheme.typography.body2.copy(color = Color(0XFFEFEEE0)),
                            modifier = Modifier.padding(top = 10.dp)
                        )
                        Spacer(Modifier.height(40.dp).fillMaxWidth())
                        OptionChips(onPlayAllClicked, chartDetails.tracks?.items ?: emptyList())
                    }

                }
            }
        }
        items(chartDetails.tracks?.items ?: emptyList()) { track ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .fillMaxWidth()
                    .background(if (track.track?.id.orEmpty() == selectedTrack.value) Color(0xCCFACD66) else Color(0xFF33373B))
                    .padding(16.dp)
                    .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() })
                { onPlayTrack(track.track?.id.orEmpty(), chartDetails.tracks?.items ?: mutableListOf()) }
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    val active by remember { mutableStateOf(false) }
                    val painter =
                        rememberAsyncImagePainter(track.track?.album?.images?.first()?.url.orEmpty())
                    Box(modifier = Modifier
                        .clickable {
                            onPlayTrack(track.track?.id.orEmpty(), chartDetails.tracks?.items ?: mutableListOf())
                        }) {
                        Image(
                            painter,
                            track.track?.album?.images?.first()?.url.orEmpty(),
                            modifier = Modifier.clip(RoundedCornerShape(5.dp)).width(40.dp)
                                .height(40.dp),
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
                            text = track.track?.name.orEmpty(),
                            style = MaterialTheme.typography.caption.copy(
                                color = Color(
                                    0XFFEFEEE0
                                )
                            )
                        )
                        Text(
                            text = track.track?.artists?.map { it.name }?.joinToString(",")
                                .orEmpty(),
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


