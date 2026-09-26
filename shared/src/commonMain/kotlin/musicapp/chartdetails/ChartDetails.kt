package musicapp.chartdetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.blur.BlurRadiusSpec
import androidx.compose.ui.graphics.blur.BlurStop
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.rememberImagePainter
import musicapp.decompose.ChartDetailsComponent
import musicapp.network.models.topfiftycharts.Item
import musicapp.network.models.topfiftycharts.TopFiftyCharts
import musicapp.player.toMediaItem
import musicapp.playerview.EqualizerBars
import musicapp_kmp.shared.generated.resources.*
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
                            it.mapNotNull { item ->
                                item.track?.toMediaItem()
                            }
                        )
                    )
                },
                onPlayTrack = { id, list ->
                    chartDetailsComponent.onOutPut(
                        ChartDetailsComponent.Output.OnTrackSelected(id, list.mapNotNull { it.track?.toMediaItem() })
                    )
                }
            )
    }
    val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(topInset + 64.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xE61D2123),
                        Color.Transparent
                    )
                )
            )
            .blur {
                radius = BlurRadiusSpec.verticalGradient(
                    startRadius = musicapp.utils.AppBlurConfig.topBarBackdropBlurRadius,
                    endRadius = 0.dp
                )
            }
    )
    IconButton(
        onClick = { chartDetailsComponent.onOutPut(ChartDetailsComponent.Output.GoBack) },
        modifier = Modifier.padding(top = topInset + 4.dp, start = 16.dp, end = 16.dp).size(32.dp)
    ) {
        Icon(
            Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(Res.string.go_back),
            tint = MaterialTheme.colors.primary,
        )
    }


    if (sleepTimerModalBottomSheetState)
        SleepTimerModalBottomSheet(
            countdownViewModel = chartDetailsComponent.countdownViewModel,
            onSleepTimerExpired = {
                chartDetailsComponent.onSleepTimerExpired()
            },
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
            color = MaterialTheme.colors.primary,
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
    playingTrackId: String
) {

    val selectedTrack = remember { mutableStateOf(playingTrackId) }
    val (painter, playlistCoverPainter) = backgroundImage(chartDetails, playingTrackId)

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
            modifier = Modifier.fillMaxSize().blur {
                radius = BlurRadiusSpec.verticalGradient(
                    startRadius = 0.dp,
                    endRadius = musicapp.utils.AppBlurConfig.imageCardBlurRadius
                )
            },
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

        val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 30.dp)
                .blur {
                    radius = BlurRadiusSpec.verticalGradient(musicapp.utils.AppBlurConfig.headerBlurStops)
                },
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                Image(
                    painter = playlistCoverPainter,
                    contentDescription = chartDetails.images?.first()?.url.orEmpty(),
                    modifier = Modifier.padding(top = topInset + 44.dp, bottom = 24.dp).fillMaxWidth()
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
                        tint = MaterialTheme.colors.primary,
                        contentDescription = stringResource(Res.string.sleep_timer),
                        modifier = Modifier.size(40.dp).padding(start = 16.dp)
                            .clickable(onClick = {
                                onSleepTimerClicked()
                            })
                    )
                }
            }
            itemsIndexed(chartDetails.tracks?.items ?: emptyList()) { index, track ->
                val isCurrentTrack = track.track?.id.orEmpty() == selectedTrack.value
                val titleColor = if (isCurrentTrack) Color(0xFF1D2123) else Color(0XFFEFEEE0)
                val subtitleColor = if (isCurrentTrack) Color(0xFF33373B) else Color(0XFFEFEEE0)

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .fillMaxWidth().background(
                            if (isCurrentTrack) Color(0xCCFACD66)
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
                            rememberImagePainter(track.track?.album?.images?.first()?.url.orEmpty())
                        Box(
                            modifier = Modifier
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
                                    tint = MaterialTheme.colors.primary,
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
                                    color = titleColor,
                                    fontWeight = if (isCurrentTrack) FontWeight.Bold else FontWeight.Normal
                                ),
                                maxLines = 1,
                                overflow = Ellipsis
                            )
                            Text(
                                text = track.track?.artists?.map { it.name }?.joinToString(", ")
                                    .orEmpty(),
                                style = MaterialTheme.typography.caption.copy(
                                    color = subtitleColor
                                ),
                                modifier = Modifier.padding(top = 8.dp),
                                maxLines = 1,
                                overflow = Ellipsis
                            )
                        }
                        if (isCurrentTrack) {
                            EqualizerBars(
                                isPlaying = true,
                                modifier = Modifier.align(Alignment.CenterVertically).padding(end = 8.dp),
                                color = Color(0xFF1D2123),
                                maxHeight = 14.dp
                            )
                        }
                        val totalSeconds = (track.track?.durationMs ?: 0) / 1000
                        val minutes = totalSeconds / 60
                        val seconds = totalSeconds % 60
                        Text(
                            text = "$minutes:${seconds.toString().padStart(2, '0')}",
                            style = MaterialTheme.typography.caption.copy(color = titleColor),
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
                tint = MaterialTheme.colors.primary,
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
