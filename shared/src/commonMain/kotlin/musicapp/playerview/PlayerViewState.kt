package musicapp.playerview

import musicapp.player.TrackItem


/**
 * Created by abdulbasit on 09/04/2023.
 */
data class PlayerViewState(
    val trackList: List<TrackItem>,
    val playingTrackId: String = "",
    val currentPosition: Long = 0,
    val isPlaying: Boolean = false,
    val duration: Long? = null,
    val isBuffering: Boolean = false,
    val errorState: Boolean = false
)
