package musicapp.playerview

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.rememberAsyncImagePainter
import musicapp.decompose.PlayerComponent
import musicapp.network.models.topfiftycharts.Item
import musicapp.player.MediaPlayerController
import musicapp.player.MediaPlayerListener
import musicapp_kmp.shared.generated.resources.Res
import musicapp_kmp.shared.generated.resources.back
import musicapp_kmp.shared.generated.resources.forward
import musicapp_kmp.shared.generated.resources.play
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource


@OptIn(ExperimentalResourceApi::class)
@Composable
internal fun PlayerView(playerComponent: PlayerComponent) {
    val state = playerComponent.viewModel.playerViewState.collectAsState()
    val mediaPlayerController = state.value.mediaPlayerController
    val selectedTrackPlaying = state.value.playingTrackId
    val trackList = state.value.trackList

    val selectedIndex = remember { mutableStateOf(0) }
    val isLoading = remember { mutableStateOf(true) }
    val selectedTrack = trackList[selectedIndex.value]

    //the index was not getting reset
    LaunchedEffect(trackList) { selectedIndex.value = 0 }

    LaunchedEffect(selectedTrackPlaying) {
        if (selectedTrackPlaying.isEmpty().not())
            selectedIndex.value =
                trackList.indexOfFirst { item -> item.track?.id.orEmpty() == selectedTrackPlaying }
    }


    LaunchedEffect(selectedTrack) {
        playerComponent.onOutPut(PlayerComponent.Output.OnTrackUpdated(selectedTrack.track?.id.orEmpty()))
    }

    playTrack(selectedTrack, mediaPlayerController, isLoading, selectedIndex, trackList)

    Box(
        modifier = Modifier.fillMaxWidth().background(Color(0xCC101010))
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)
            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { }
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            val painter = rememberAsyncImagePainter(
                selectedTrack.track?.album?.images?.first()?.url.orEmpty()
            )
            Box(modifier = Modifier.clip(RoundedCornerShape(5.dp)).width(49.dp).height(49.dp)) {
                Image(
                    painter,
                    selectedTrack.track?.album?.images?.first()?.url.orEmpty(),
                    modifier = Modifier.clip(RoundedCornerShape(5.dp)).width(49.dp).height(49.dp),
                    contentScale = ContentScale.Crop
                )
                if (isLoading.value) {
                    Box(modifier = Modifier.fillMaxSize().background(Color(0x80000000))) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center).padding(8.dp),
                            color = Color(0xFFFACD66),
                        )
                    }
                }
            }
            Column(Modifier.weight(1f).padding(start = 8.dp).align(Alignment.Top)) {
                Text(
                    text = selectedTrack.track?.name.orEmpty(),
                    style = MaterialTheme.typography.caption.copy(
                        color = Color(
                            0XFFEFEEE0
                        )
                    )
                )
                Text(
                    text = selectedTrack.track?.artists?.map { it.name }?.joinToString(",")
                        .orEmpty(),
                    style = MaterialTheme.typography.caption.copy(
                        color = Color(
                            0XFFEFEEE0
                        )
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            Row(modifier = Modifier.align(Alignment.CenterVertically)) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    tint = Color(0xFFFACD66),
                    contentDescription = stringResource(Res.string.back),
                    modifier = Modifier.padding(end = 8.dp).size(32.dp)
                        .align(Alignment.CenterVertically)
                        .clickable(onClick = {
                            if (selectedIndex.value - 1 >= 0) {
                                selectedIndex.value -= 1
                            }
                        })
                )
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    tint = Color(0xFFFACD66),
                    contentDescription = stringResource(Res.string.play),
                    modifier = Modifier.padding(end = 8.dp).size(32.dp)
                        .align(Alignment.CenterVertically)
                        .clickable(onClick = {
                            if (mediaPlayerController.isPlaying()) {
                                mediaPlayerController.pause()
                            } else {
                                mediaPlayerController.start()
                            }
                        })
                )
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    tint = Color(0xFFFACD66),
                    contentDescription = stringResource(Res.string.forward),
                    modifier = Modifier.padding(end = 8.dp).size(32.dp)
                        .align(Alignment.CenterVertically)
                        .clickable(onClick = {
                            if (selectedIndex.value < trackList.size - 1) {
                                selectedIndex.value += 1
                            }
                        })
                )
            }
        }
    }
}

private fun playTrack(
    selectedTrack: Item,
    mediaPlayerController: MediaPlayerController,
    isLoading: MutableState<Boolean>,
    selectedIndex: MutableState<Int>,
    trackList: List<Item>,
) {
    selectedTrack.track?.previewUrl?.let {
        mediaPlayerController.prepare(it, listener = object : MediaPlayerListener {
            override fun onReady() {
                isLoading.value = false
            }

            override fun onAudioCompleted() {
                if (selectedIndex.value < trackList.size - 1) {
                    selectedIndex.value += 1
                }
            }

            override fun onError() {
                if (selectedIndex.value < trackList.size - 1) {
                    selectedIndex.value += 1
                }
            }
        })
    } ?: run {
        if (selectedIndex.value < trackList.size - 1) {
            selectedIndex.value += 1
        } else {
            // selectedIndex.value = 0
        }
    }
}