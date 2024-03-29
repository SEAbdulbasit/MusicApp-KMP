package musicapp_kmp.playerview

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import musicapp_kmp.decompose.PlayerComponent
import musicapp_kmp.network.models.topfiftycharts.Item
import musicapp_kmp.player.MediaPlayerController
import org.koin.core.component.KoinComponent


/**
 * Created by abdulbasit on 26/02/2023.
 */
class PlayerViewModel(
    mediaPlayerController: MediaPlayerController,
    trackList: List<Item>,
    playerInputs: SharedFlow<PlayerComponent.Input>
) : KoinComponent {
    private val viewModelScope = CoroutineScope(Dispatchers.Unconfined)
    val chartDetailsViewState = MutableStateFlow(
        PlayerViewState(
            trackList = trackList, mediaPlayerController = mediaPlayerController
        )
    )

    init {
        viewModelScope.launch {
            playerInputs.collectLatest {
                when (it) {
                    is PlayerComponent.Input.PlayTrack -> chartDetailsViewState.value =
                        chartDetailsViewState.value.copy(playingTrackId = it.trackId)

                    is PlayerComponent.Input.UpdateTracks -> chartDetailsViewState.value =
                        chartDetailsViewState.value.copy(trackList = it.tracksList)
                }
            }
        }
    }

//    override fun onDestroy() {
//        viewModelScope.cancel()
//    }

    companion object {

        var viewModel: PlayerViewModel? = null

        fun getViewModel(
            mediaPlayerController: MediaPlayerController,
            trackList: List<Item>,
            playerInputs: SharedFlow<PlayerComponent.Input>
        ): PlayerViewModel {
            if (viewModel == null) {
                viewModel = PlayerViewModel(
                    mediaPlayerController = mediaPlayerController,
                    trackList = trackList,
                    playerInputs = playerInputs
                )
            }
            return viewModel as PlayerViewModel
        }
    }
}