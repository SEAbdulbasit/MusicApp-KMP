package com.example.musicapp_kmp.playerview

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.musicapp_kmp.network.models.topfiftycharts.Item
import com.seiko.imageloader.rememberAsyncImagePainter


@Composable
internal fun PlayerView(trackList: List<Item>) {
    val selectedIndex = remember { mutableStateOf(0) }
    val selectedTrack = trackList[selectedIndex.value]
    Box(
        modifier = Modifier.fillMaxWidth().background(Color(0x80101010))
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 56.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            val painter = rememberAsyncImagePainter(
                selectedTrack.track?.album?.images?.first()?.url
                    ?: "https://www.linkpicture.com/q/vladimir-haltakov-PMfuunAfF2w-unsplash.jpg"
            )
            Image(
                painter,
                selectedTrack.track?.album?.images?.first()?.url
                    ?: "https://www.linkpicture.com/q/vladimir-haltakov-PMfuunAfF2w-unsplash.jpg",
                modifier = Modifier.clip(RoundedCornerShape(5.dp)).width(40.dp).height(40.dp),
                contentScale = ContentScale.Crop
            )
            Column(Modifier.weight(1f).padding(start = 8.dp).align(Alignment.Top)) {
                Text(
                    text = selectedTrack.track?.name ?: "",
                    style = MaterialTheme.typography.caption.copy(
                        color = Color(
                            0XFFEFEEE0
                        )
                    )
                )
                Text(
                    text = selectedTrack.track?.album?.name ?: "",
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
                    imageVector = Icons.Default.PlayArrow,
                    tint = Color(0xFFFACD66),
                    contentDescription = "Play All",
                    modifier = Modifier.padding(end = 8.dp).size(16.dp)
                        .align(Alignment.CenterVertically)
                )
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    tint = Color(0xFFFACD66),
                    contentDescription = "Forward",
                    modifier = Modifier.padding(end = 8.dp).size(16.dp)
                        .align(Alignment.CenterVertically).clickable(onClick = {
                            if (selectedIndex.value < trackList.size - 1) {
                                selectedIndex.value = selectedIndex.value + 1
                            }
                        })
                )
            }
        }
    }

}