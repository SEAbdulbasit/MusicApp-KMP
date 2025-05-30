package musicapp.decompose

import musicapp.player.TrackItem
import musicapp.playerview.PlayerViewModel


/**
 * Created by abdulbasit on 19/03/2023.
 */

interface PlayerComponent {
    val viewModel: PlayerViewModel

    fun onOutPut(output: Output)

    sealed class Output {
        data object OnPause : Output()
        data object OnPlay : Output()
        data class OnTrackUpdated(val trackId: String) : Output()
    }

    sealed interface Input {
        data class PlayTrack(val trackId: String, val tracksList: List<TrackItem>) : Input
        data class UpdateTracks(val tracksList: List<TrackItem>) : Input
    }

}
