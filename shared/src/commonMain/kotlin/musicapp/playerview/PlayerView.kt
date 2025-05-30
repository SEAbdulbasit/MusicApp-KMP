package musicapp.playerview

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.rememberImagePainter
import musicapp.decompose.PlayerComponent
import musicapp.theme.*
import musicapp_kmp.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource


@Composable
internal fun PlayerView(playerComponent: PlayerComponent) {
    playerComponent.viewModel.syncWithMediaPlayer()

    val state = playerComponent.viewModel.playerViewState.collectAsState()
    val trackList = state.value.trackList
    val isPlaying = state.value.isPlaying
    val currentTrackId = state.value.playingTrackId
    val isBuffering = state.value.isBuffering
    val isError = state.value.errorState

    if (trackList.isEmpty()) return

    val currentIndex = playerComponent.viewModel.getCurrentTrackIndex()
    val currentTrack = if (currentIndex >= 0) trackList[currentIndex] else return

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

    Box(
        modifier = Modifier.fillMaxWidth().background(playerBackgroundColor)
            .padding(spacingMedium).clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }) { }) {

        Row(modifier = Modifier.fillMaxWidth()) {
            val painter = rememberImagePainter(
                url = currentTrack.albumImageUrl,
            )
            Box(modifier = Modifier.clip(RoundedCornerShape(borderRadiusSmall)).width(49.dp).height(49.dp)) {
                Image(
                    painter = painter,
                    contentDescription = currentTrack.albumImageUrl,
                    modifier = Modifier.clip(RoundedCornerShape(borderRadiusSmall)).width(49.dp).height(49.dp),
                    contentScale = ContentScale.Crop
                )
                if (isBuffering) {
                    Box(modifier = Modifier.fillMaxSize().background(loadingOverlayColor)) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center).padding(spacingSmall),
                            color = accentColor,
                        )
                    }
                }
            }
            Column(Modifier.weight(1f).padding(start = spacingSmall).align(Alignment.Top)) {
                Text(
                    text = currentTrack.title, style = MaterialTheme.typography.subtitle1.copy(
                        color = textColor
                    ),
                    modifier = Modifier.fillMaxWidth().basicMarquee(Int.MAX_VALUE)
                )
                Text(
                    text = currentTrack.artist,
                    style = MaterialTheme.typography.subtitle1.copy(
                        color = textColor
                    ),
                    modifier = Modifier.padding(top = spacingSmall)
                )
            }
            Row(modifier = Modifier.align(Alignment.CenterVertically)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    tint = accentColor,
                    contentDescription = stringResource(Res.string.back),
                    modifier = Modifier.padding(end = spacingSmall).size(iconSizeMedium)
                        .align(Alignment.CenterVertically)
                        .clickable(onClick = {
                            playerComponent.viewModel.setBuffering(true)
                            playerComponent.viewModel.playPreviousTrack()
                        })
                )
                Icon(
                    painter = painterResource(Res.drawable.rewind),
                    tint = accentColor,
                    contentDescription = stringResource(Res.string.rewind_5_sec),
                    modifier = Modifier
                        .padding(end = spacingSmall)
                        .size(iconSizeMedium)
                        .align(Alignment.CenterVertically)
                        .clickable(onClick = {
                            playerComponent.viewModel.rewind5Seconds()
                        })
                )
                PlayPauseButton(
                    modifier = Modifier.padding(end = spacingSmall).size(iconSizeMedium)
                        .align(Alignment.CenterVertically),
                    isPlaying = isPlaying,
                    onTogglePlayPause = { playerComponent.viewModel.togglePlayPause() }
                )
                Icon(
                    painter = painterResource(Res.drawable.forward),
                    tint = accentColor,
                    contentDescription = stringResource(Res.string.forward_5_sec),
                    modifier = Modifier
                        .padding(end = spacingSmall)
                        .size(iconSizeMedium)
                        .align(Alignment.CenterVertically)
                        .clickable(onClick = {
                            playerComponent.viewModel.forward5Seconds()
                        })
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    tint = accentColor,
                    contentDescription = stringResource(Res.string.forward),
                    modifier = Modifier.padding(end = spacingSmall).size(iconSizeMedium)
                        .align(Alignment.CenterVertically)
                        .clickable(onClick = {
                            playerComponent.viewModel.setBuffering(true)
                            playerComponent.viewModel.playNextTrack()
                        })
                )
            }
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
        tint = accentColor,
        contentDescription = stringResource(Res.string.pause),
        modifier = modifier.clickable(onClick = onTogglePlayPause)
    ) else Icon(
        imageVector = Icons.Filled.PlayArrow,
        tint = accentColor,
        contentDescription = stringResource(Res.string.play),
        modifier = modifier.clickable(onClick = onTogglePlayPause)
    )
}