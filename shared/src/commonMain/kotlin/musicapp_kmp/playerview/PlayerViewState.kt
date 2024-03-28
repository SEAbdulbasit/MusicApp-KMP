package musicapp_kmp.playerview

import musicapp_kmp.network.models.topfiftycharts.Item
import musicapp_kmp.player.MediaPlayerController


/**
 * Created by abdulbasit on 09/04/2023.
 */
data class PlayerViewState(
    val trackList: List<Item>,
    val mediaPlayerController: MediaPlayerController,
    val playingTrackId: String = ""
)

