package musicapp_kmp.decompose

import musicapp_kmp.network.models.topfiftycharts.Item
import musicapp_kmp.playerview.PlayerViewModel


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
        data class PlayTrack(val trackId: String) : Input
        data class UpdateTracks(val tracksList: List<Item>) : Input
    }

}
