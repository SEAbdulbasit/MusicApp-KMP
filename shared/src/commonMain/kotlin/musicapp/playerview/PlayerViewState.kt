package musicapp.playerview

import musicapp.network.models.topfiftycharts.Item
import musicapp.player.MediaPlayerController


/**
 * Created by abdulbasit on 09/04/2023.
 */
data class PlayerViewState(
    val trackList: List<Item>,
    val mediaPlayerController: MediaPlayerController,
    val playingTrackId: String = ""
)

