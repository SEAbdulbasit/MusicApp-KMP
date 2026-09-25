package musicapp.playerview

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.blur.BlurRadiusSpec
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.seiko.imageloader.rememberImagePainter
import musicapp.decompose.PlayerComponent
import musicapp.player.TrackItem
import musicapp.theme.*
import musicapp.utils.shimmer
import musicapp_kmp.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

@Composable
internal fun PlayerView(playerComponent: PlayerComponent) {

    val state = playerComponent.viewModel.playerViewState.collectAsState()
    val trackList = state.value.trackList
    val isPlaying = state.value.isPlaying
    val currentTrackId = state.value.playingTrackId
    val isBuffering = state.value.isBuffering
    val isError = state.value.errorState
    val currentPosition = state.value.currentPosition
    val duration = state.value.duration ?: 0L

    if (trackList.isEmpty()) return

    val currentIndex = playerComponent.viewModel.getCurrentTrackIndex()
    val currentTrack = if (currentIndex >= 0) trackList[currentIndex] else return

    LaunchedEffect(Unit){
        playerComponent.viewModel.syncWithMediaPlayer()
    }

    LaunchedEffect(isError) {
        if (isError) {
            playerComponent.viewModel.setBuffering(true)
            playerComponent.viewModel.playNextTrack()
        }
    }

    LaunchedEffect(currentTrackId) {
        if (currentTrackId.isNotEmpty()) {
            playerComponent.onOutPut(PlayerComponent.Output.OnTrackUpdated(currentTrackId))
        }
    }

    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            playerComponent.viewModel.syncWithMediaPlayer()
            delay(1000)
        }
    }

    var isExpanded by remember { mutableStateOf(false) }

    AnimatedVisibility(
        visible = isExpanded,
        enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
        exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top)
    ) {
        FullScreenPlayer(
            currentTrack = currentTrack,
            isPlaying = isPlaying,
            isBuffering = isBuffering,
            currentPosition = currentPosition,
            duration = duration,
            onCollapse = { isExpanded = false },
            onPlayPause = { playerComponent.viewModel.togglePlayPause() },
            onPrevious = {
                playerComponent.viewModel.setBuffering(true)
                playerComponent.viewModel.playPreviousTrack()
            },
            onNext = {
                playerComponent.viewModel.setBuffering(true)
                playerComponent.viewModel.playNextTrack()
            },
            onRewind = { playerComponent.viewModel.rewind5Seconds() },
            onForward = { playerComponent.viewModel.forward5Seconds() },
            onSeek = { playerComponent.viewModel.seekTo(it) },
            onClose = {
                playerComponent.viewModel.closePlayer()
                playerComponent.onOutPut(PlayerComponent.Output.OnClose)
            }
        )
    }

    AnimatedVisibility(
        visible = !isExpanded,
        enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom),
        exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom)
    ) {
        CompactPlayer(
            currentTrack = currentTrack,
            isPlaying = isPlaying,
            isBuffering = isBuffering,
            currentPosition = currentPosition,
            duration = duration,
            onExpand = { isExpanded = true },
            onPlayPause = { playerComponent.viewModel.togglePlayPause() },
            onPrevious = {
                playerComponent.viewModel.setBuffering(true)
                playerComponent.viewModel.playPreviousTrack()
            },
            onNext = {
                playerComponent.viewModel.setBuffering(true)
                playerComponent.viewModel.playNextTrack()
            },
            onClose = {
                playerComponent.viewModel.closePlayer()
                playerComponent.onOutPut(PlayerComponent.Output.OnClose)
            }
        )
    }
}

@Composable
internal fun CompactPlayer(
    currentTrack: TrackItem,
    isPlaying: Boolean,
    isBuffering: Boolean,
    currentPosition: Long,
    duration: Long,
    onExpand: () -> Unit,
    onPlayPause: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onClose: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    var horizontalDragOffset by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xE625292B))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onExpand
            )
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (horizontalDragOffset < -50f) {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onNext()
                        } else if (horizontalDragOffset > 50f) {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onPrevious()
                        }
                        horizontalDragOffset = 0f
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        horizontalDragOffset += dragAmount
                    }
                )
            }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val painter = rememberImagePainter(url = currentTrack.albumImageUrl)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .size(46.dp)
                ) {
                    Image(
                        painter = painter,
                        contentDescription = currentTrack.albumImageUrl,
                        modifier = Modifier.fillMaxSize().shimmer(),
                        contentScale = ContentScale.Crop
                    )
                    if (isBuffering) {
                        Box(modifier = Modifier.fillMaxSize().background(loadingOverlayColor)) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center).padding(spacingSmall),
                                color = MaterialTheme.colors.primary,
                                strokeWidth = 2.5.dp
                            )
                        }
                    }
                }
                Column(
                    Modifier
                        .weight(1f)
                        .padding(horizontal = 10.dp)
                        .align(Alignment.CenterVertically)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isPlaying) {
                            EqualizerBars(
                                isPlaying = true,
                                modifier = Modifier.padding(end = 6.dp),
                                maxHeight = 12.dp
                            )
                        }
                        Text(
                            text = currentTrack.title,
                            style = MaterialTheme.typography.subtitle1.copy(
                                color = MaterialTheme.colors.onSurface,
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier.weight(1f, fill = false).basicMarquee(Int.MAX_VALUE),
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Text(
                        text = currentTrack.artist.replace(",", ", "),
                        style = MaterialTheme.typography.caption.copy(
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.padding(top = 2.dp).basicMarquee(Int.MAX_VALUE),
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        tint = MaterialTheme.colors.primary,
                        contentDescription = stringResource(Res.string.back),
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onPrevious()
                            }
                    )
                    PlayPauseButton(
                        modifier = Modifier.size(32.dp),
                        isPlaying = isPlaying,
                        onTogglePlayPause = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onPlayPause()
                        }
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        tint = MaterialTheme.colors.primary,
                        contentDescription = stringResource(Res.string.forward),
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onNext()
                            }
                    )
                    Icon(
                        imageVector = Icons.Default.Close,
                        tint = MaterialTheme.colors.onSurface.copy(alpha = 0.5f),
                        contentDescription = "Close Player",
                        modifier = Modifier
                            .padding(start = 2.dp)
                            .size(20.dp)
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onClose()
                            }
                    )
                }
            }
            val progress = if (duration > 0) (currentPosition.toFloat() / duration.toFloat()).coerceIn(0f, 1f) else 0f
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.5.dp),
                color = MaterialTheme.colors.primary,
                backgroundColor = Color(0x22FACD66)
            )
        }
    }
}

@Composable
internal fun FullScreenPlayer(
    currentTrack: TrackItem,
    isPlaying: Boolean,
    isBuffering: Boolean,
    currentPosition: Long,
    duration: Long,
    onCollapse: () -> Unit,
    onPlayPause: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onRewind: () -> Unit,
    onForward: () -> Unit,
    onSeek: (Long) -> Unit,
    onClose: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    var verticalDragOffset by remember { mutableStateOf(0f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .systemBarsPadding()
            .offset { IntOffset(0, verticalDragOffset.coerceAtLeast(0f).roundToInt()) }
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragEnd = {
                        if (verticalDragOffset > 100f) {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onCollapse()
                        }
                        verticalDragOffset = 0f
                    },
                    onVerticalDrag = { _, dragAmount ->
                        verticalDragOffset += dragAmount
                    }
                )
            }
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(modifier = Modifier.zIndex(90f).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Collapse",
                tint = MaterialTheme.colors.onBackground,
                modifier = Modifier
                    .size(32.dp)
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onCollapse()
                    }
            )
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = MaterialTheme.colors.onBackground,
                modifier = Modifier
                    .size(32.dp)
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onClose()
                    }
            )
        }
        Spacer(modifier = Modifier.height(28.dp))
        
        val painter = rememberImagePainter(url = currentTrack.albumImageUrl)

        Box(
            contentAlignment = Alignment.Center
        ) {
            // Ambient Glow behind album artwork
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colors.primary.copy(alpha = 0.35f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
                    .blur(28.dp)
            )
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(32.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painter,
                    contentDescription = currentTrack.albumImageUrl,
                    modifier = Modifier.fillMaxSize().shimmer(),
                    contentScale = ContentScale.Crop
                )
                if (isBuffering) {
                    Box(modifier = Modifier.fillMaxSize().background(loadingOverlayColor)) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center).padding(spacingMedium),
                            color = MaterialTheme.colors.primary,
                            strokeWidth = 4.dp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = currentTrack.title,
            style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colors.onBackground,
            modifier = Modifier.fillMaxWidth().basicMarquee(Int.MAX_VALUE),
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = currentTrack.artist.replace(",", ", "),
            style = MaterialTheme.typography.subtitle2,
            color = MaterialTheme.colors.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.padding(top = 4.dp).fillMaxWidth().basicMarquee(Int.MAX_VALUE),
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis
        )
        
        Spacer(modifier = Modifier.weight(1f))

        val progress = if (duration > 0) (currentPosition.toFloat() / duration.toFloat()).coerceIn(0f, 1f) else 0f
        
        fun Long.formatTime(): String {
            val totalSeconds = this / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
        }

        Slider(
            value = progress,
            onValueChange = { newProgress ->
                if (duration > 0) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onSeek((newProgress * duration).toLong())
                }
            },
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colors.primary,
                activeTrackColor = MaterialTheme.colors.primary,
                inactiveTrackColor = MaterialTheme.colors.onSurface.copy(alpha = 0.3f)
            ),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
        )
        Row(modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 24.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(currentPosition.formatTime(), style = MaterialTheme.typography.caption)
            Text(duration.formatTime(), style = MaterialTheme.typography.caption)
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 48.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                tint = MaterialTheme.colors.primary,
                contentDescription = stringResource(Res.string.back),
                modifier = Modifier.size(36.dp).clickable {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onPrevious()
                }
            )
            Icon(
                painter = painterResource(Res.drawable.rewind),
                tint = MaterialTheme.colors.primary,
                contentDescription = stringResource(Res.string.rewind_5_sec),
                modifier = Modifier.clip(CircleShape).size(36.dp).clickable {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onRewind()
                }
            )
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colors.primary)
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onPlayPause()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = if (isPlaying) painterResource(Res.drawable.baseline_pause_24) else rememberVectorPainter(Icons.Filled.PlayArrow),
                    tint = MaterialTheme.colors.onPrimary,
                    contentDescription = if (isPlaying) stringResource(Res.string.pause) else stringResource(Res.string.play),
                    modifier = Modifier.size(36.dp)
                )
            }
            Icon(
                painter = painterResource(Res.drawable.forward),
                tint = MaterialTheme.colors.primary,
                contentDescription = stringResource(Res.string.forward_5_sec),
                modifier = Modifier.clip(CircleShape).size(36.dp).clickable {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onForward()
                }
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                tint = MaterialTheme.colors.primary,
                contentDescription = stringResource(Res.string.forward),
                modifier = Modifier.size(36.dp).clickable {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onNext()
                }
            )
        }
    }
}

@Composable
fun PlayPauseButton(
    modifier: Modifier,
    isPlaying: Boolean,
    onTogglePlayPause: () -> Unit
) {
    if (isPlaying) Icon(
        painter = painterResource(Res.drawable.baseline_pause_24),
        tint = MaterialTheme.colors.primary,
        contentDescription = stringResource(Res.string.pause),
        modifier = modifier.clickable(onClick = onTogglePlayPause)
    ) else Icon(
        imageVector = Icons.Filled.PlayArrow,
        tint = MaterialTheme.colors.primary,
        contentDescription = stringResource(Res.string.play),
        modifier = modifier.clickable(onClick = onTogglePlayPause)
    )
}
