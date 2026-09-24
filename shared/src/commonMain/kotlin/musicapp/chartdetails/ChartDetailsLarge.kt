package musicapp.chartdetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
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
internal fun ChartDetailsScreenLarge(
    chartDetailsComponent: ChartDetailsComponent,
) {
    val state = chartDetailsComponent.viewModel.chartDetailsViewState.collectAsState()
    var sleepTimerModalBottomSheetState by remember { mutableStateOf(false) }
    var isAnySleepTimerSelected by remember { mutableStateOf(false) }

    when (val resultedState = state.value) {
        is ChartDetailsViewState.Failure -> Failure(resultedState.error)
        ChartDetailsViewState.Loading -> Loading()
        is ChartDetailsViewState.Success -> ChartDetailsViewLarge(
            isAnyTimeIntervalSelected = isAnySleepTimerSelected,
            chartDetails = resultedState.chartDetails,
            playingTrackId = resultedState.playingTrackId,
            onSleepTimerClicked = {
                sleepTimerModalBottomSheetState = true
            },
            onPlayAllClicked = {
                chartDetailsComponent.onOutPut(
                    ChartDetailsComponent.Output.OnPlayAllSelected(
                        it.mapNotNull { it.track?.toMediaItem() }
                    )
                )
            },
            onPlayTrack = { id, list ->
                chartDetailsComponent.onOutPut(
                    ChartDetailsComponent.Output.OnTrackSelected(
                        id,
                        list.mapNotNull { it.track?.toMediaItem() }
                    )
                )
            }
        )
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
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
                    startRadius = 24.dp,
                    endRadius = 0.dp
                )
            }
    )
    IconButton(onClick = { chartDetailsComponent.onOutPut(ChartDetailsComponent.Output.GoBack) }) {
        Icon(
            Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(Res.string.forward),
            tint = MaterialTheme.colors.primary,
            modifier = Modifier.padding(all = 16.dp).size(32.dp)
        )
    }

    if (sleepTimerModalBottomSheetState)
        SleepTimerModalBottomSheet(
            countdownViewModel = chartDetailsComponent.countdownViewModel,
            onSleepTimerExpired = { chartDetailsComponent.onSleepTimerExpired() },
            onDismiss = {
                sleepTimerModalBottomSheetState = false
            },
            isAnyTimeIntervalSelected = { anyTimeIntervalSelected ->
                isAnySleepTimerSelected = anyTimeIntervalSelected
            })
}

@OptIn(ExperimentalResourceApi::class)
@Composable
internal fun ChartDetailsViewLarge(
    chartDetails: TopFiftyCharts,
    isAnyTimeIntervalSelected: Boolean,
    onPlayAllClicked: (List<Item>) -> Unit,
    onPlayTrack: (String, List<Item>) -> Unit,
    onSleepTimerClicked: () -> Unit,
    playingTrackId: String
) {
    val (painter, playlistCoverPainter) = backgroundImage(chartDetails, playingTrackId)

    val selectedTrack = remember { mutableStateOf(playingTrackId) }

    val sleepTimerIcon = if (isAnyTimeIntervalSelected)
        painterResource(Res.drawable.moon_fill)
    else
        painterResource(Res.drawable.moon_outline)

    LaunchedEffect(playingTrackId) {
        selectedTrack.value = playingTrackId
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter,
            chartDetails.images?.first()?.url.orEmpty(),
            modifier = Modifier.fillMaxSize().blur {
                radius = BlurRadiusSpec.verticalGradient(
                    startRadius = 0.dp,
                    endRadius = 24.dp
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
    }

    LazyColumn(
        modifier = Modifier
            .padding(horizontal = 63.dp)
            .blur {
                radius = BlurRadiusSpec.verticalGradient(
                    listOf(
                        BlurStop(fraction = 0.0f, radius = 20.dp),
                        BlurStop(fraction = 0.12f, radius = 0.dp),
                        BlurStop(fraction = 1.0f, radius = 0.dp)
                    )
                )
            },
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {

        item {
            Box(modifier = Modifier.fillMaxSize()) {
                Row(modifier = Modifier.padding(16.dp).align(Alignment.TopCenter)) {
                    Image(
                        painter = playlistCoverPainter,
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
                            text = "${chartDetails.tracks?.items?.size ?: 0} ${stringResource(Res.string.songs)}}",
                            style = MaterialTheme.typography.body2.copy(color = Color(0XFFEFEEE0)),
                            modifier = Modifier.padding(top = 10.dp)
                        )
                        Spacer(Modifier.height(40.dp).fillMaxWidth())
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

                }
            }
        }
        items(chartDetails.tracks?.items ?: emptyList()) { track ->
            val isCurrentTrack = track.track?.id.orEmpty() == selectedTrack.value
            val titleColor = if (isCurrentTrack) Color(0xFF1D2123) else Color(0XFFEFEEE0)
            val subtitleColor = if (isCurrentTrack) Color(0xFF33373B) else Color(0XFFEFEEE0)

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .fillMaxWidth()
                    .background(
                        if (isCurrentTrack) Color(
                            0xCCFACD66
                        ) else Color(0xFF33373B)
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
                Row(modifier = Modifier.fillMaxWidth()) {
                    val active by remember { mutableStateOf(false) }
                    val albumImageUrl = rememberImagePainter(track.track?.album?.images?.first()?.url.orEmpty())
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
                            )
                        )
                        Text(
                            text = track.track?.artists?.joinToString(",") { it.name ?: "" }
                                .orEmpty(),
                            style = MaterialTheme.typography.caption.copy(
                                color = subtitleColor
                            ),
                            modifier = Modifier.padding(top = 8.dp)
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
                    Text(
                        text = "${(((track.track?.durationMs ?: 0) / (1000 * 60)) % 60)}:${(((track.track?.durationMs ?: 0) / (1000)) % 60)}",
                        style = MaterialTheme.typography.caption.copy(color = titleColor),
                        modifier = Modifier.align(
                            Alignment.Bottom
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun backgroundImage(
    chartDetails: TopFiftyCharts,
    playingTrackId: String
): Pair<Painter, Painter> {
    val currentTrack = chartDetails.tracks?.items?.find { it.track?.id.orEmpty() == playingTrackId }

    val backgroundImageUrl =
        currentTrack?.track?.album?.images?.firstOrNull()?.url ?: chartDetails.images?.first()?.url.orEmpty()
    val painter = rememberImagePainter(backgroundImageUrl)

    val playlistCoverPainter = rememberImagePainter(chartDetails.images?.first()?.url.orEmpty())
    return Pair(painter, playlistCoverPainter)
}
