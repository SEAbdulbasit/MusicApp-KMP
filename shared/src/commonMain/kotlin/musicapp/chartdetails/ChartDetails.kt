package musicapp.chartdetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
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
import androidx.compose.runtime.setValue
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
import musicapp_kmp.shared.generated.resources.Res
import musicapp_kmp.shared.generated.resources.go_back
import musicapp_kmp.shared.generated.resources.moon_fill
import musicapp_kmp.shared.generated.resources.moon_outline
import musicapp_kmp.shared.generated.resources.play_all
import musicapp_kmp.shared.generated.resources.sleep_timer
import musicapp_kmp.shared.generated.resources.songs
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource


/**
 * Created by abdulbasit on 28/02/2023.
 */
@OptIn(ExperimentalResourceApi::class)
@Composable
internal fun ChartDetailsScreen(
    chartDetailsComponent: ChartDetailsComponent,
) {
    val state = chartDetailsComponent.viewModel.chartDetailsViewState.collectAsState()
    var sleepTimerModalBottomSheetState by remember { mutableStateOf(false) }
    var isAnySleepTimerSelected by remember { mutableStateOf(false) }


    when (val resultedState = state.value) {
        is ChartDetailsViewState.Failure -> Failure(resultedState.error)
        ChartDetailsViewState.Loading -> Loading()
        is ChartDetailsViewState.Success ->
            ChartDetailsView(
                isAnyTimeIntervalSelected = isAnySleepTimerSelected,
                chartDetails = resultedState.chartDetails,
                playingTrackId = resultedState.playingTrackId,
                onSleepTimerClicked = {
                    sleepTimerModalBottomSheetState = true
                },
                onPlayAllClicked = {
                    chartDetailsComponent.onOutPut(
                        ChartDetailsComponent.Output.OnPlayAllSelected(
                            it
                        )
                    )
                },
                onPlayTrack = { id, list ->
                    chartDetailsComponent.onOutPut(
                        ChartDetailsComponent.Output.OnTrackSelected(id, list)
                    )
                }
            )
    }
    IconButton(
        onClick = { chartDetailsComponent.onOutPut(ChartDetailsComponent.Output.GoBack) },
        modifier = Modifier.padding(top = 40.dp, start = 16.dp, end = 16.dp).size(32.dp)
    ) {
        Icon(
            Icons.Filled.ArrowBack,
            contentDescription = stringResource(Res.string.go_back),
            tint = Color(0xFFFACD66),
        )
    }


    if (sleepTimerModalBottomSheetState)
        SleepTimerModalBottomSheet(
            countdownViewModel = chartDetailsComponent.countdownViewModel,
            mediaPlayerController = chartDetailsComponent.mediaPlayerController,
            onDismiss = {
                sleepTimerModalBottomSheetState = false
            },
            isAnyTimeIntervalSelected = { anyTimeIntervalSelected ->
                isAnySleepTimerSelected = anyTimeIntervalSelected
            })
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


@OptIn(ExperimentalResourceApi::class)
@Composable
internal fun ChartDetailsView(
    chartDetails: TopFiftyCharts,
    isAnyTimeIntervalSelected: Boolean,
    onPlayAllClicked: (List<Item>) -> Unit,
    onPlayTrack: (String, List<Item>) -> Unit,
    onSleepTimerClicked: () -> Unit,
    playingTrackId: Any
) {

    val selectedTrack = remember { mutableStateOf(playingTrackId) }
    val painter = rememberAsyncImagePainter(chartDetails.images?.first()?.url.orEmpty())

    val sleepTimerIcon = if (isAnyTimeIntervalSelected)
        painterResource(Res.drawable.moon_fill)
    else
        painterResource(Res.drawable.moon_outline)

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
                    modifier = Modifier.padding(top = 100.dp, bottom = 24.dp).fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(25.dp)),
                    contentScale = ContentScale.Crop,
                )
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
                    text = "${chartDetails.tracks?.items?.size ?: 0} ${stringResource(Res.string.songs)}",
                    style = MaterialTheme.typography.body2.copy(color = Color(0XFFEFEEE0)),
                    modifier = Modifier.padding(top = 8.dp)
                )
                Spacer(Modifier.height(32.dp).fillMaxWidth())
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OptionChips(onPlayAllClicked, chartDetails.tracks?.items ?: emptyList())
                    Icon(
                        painter = sleepTimerIcon,
                        tint = Color(0xFFFACD66),
                        contentDescription = stringResource(Res.string.sleep_timer),
                        modifier = Modifier.size(40.dp).padding(start = 16.dp)
                            .clickable(onClick = {
                                onSleepTimerClicked()
                            })
                    )
                }
            }
            itemsIndexed(chartDetails.tracks?.items ?: emptyList()) { index, track ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .fillMaxWidth().background(
                            if (track.track?.id.orEmpty() == selectedTrack.value) Color(0xCCFACD66)
                            else Color(0xFF33373B)
                        )
                        .padding(16.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() })
                        {
                            onPlayTrack(
                                track.track?.id.orEmpty(),
                                chartDetails.tracks?.items ?: mutableListOf()
                            )
                        }
                ) {
                    val active by remember { mutableStateOf(false) }
                    Row(modifier = Modifier.fillMaxWidth()) {
                        val albumImageUrl =
                            rememberAsyncImagePainter(track.track?.album?.images?.first()?.url.orEmpty())
                        Box(modifier = Modifier
                            .clickable {
                                onPlayTrack(
                                    track.track?.id.orEmpty(),
                                    chartDetails.tracks?.items ?: mutableListOf()
                                )
                            }) {
                            Image(
                                albumImageUrl,
                                track.track?.album?.images?.first()?.url.orEmpty(),
                                modifier = Modifier.clip(RoundedCornerShape(5.dp)).width(40.dp)
                                    .height(40.dp),
                                contentScale = ContentScale.Crop
                            )
                            if (active) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    tint = Color(0xFFFACD66),
                                    contentDescription = stringResource(Res.string.play_all),
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
                if (index == chartDetails.tracks?.items?.lastIndex) {
                    Column(modifier = Modifier.fillMaxWidth().height(100.dp)) { }
                }
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
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
                contentDescription = stringResource(Res.string.play_all),
                modifier = Modifier.padding(end = 8.dp).size(16.dp)
                    .align(Alignment.CenterVertically)
            )
            Text(
                text = stringResource(Res.string.play_all),
                style = MaterialTheme.typography.caption.copy(color = Color(0XFFEFEEE0))
            )
        }
    }
}