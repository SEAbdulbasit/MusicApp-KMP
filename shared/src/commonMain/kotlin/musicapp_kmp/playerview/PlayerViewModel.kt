package musicapp_kmp.playerview

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import musicapp_kmp.chartdetails.PlayerInputUsecase
import musicapp_kmp.decompose.PlayerComponent
import musicapp_kmp.network.models.topfiftycharts.Item
import musicapp_kmp.player.MediaPlayerController


/**
 * Created by abdulbasit on 26/02/2023.
 */
class PlayerViewModel(
    private val playerInputUsecase: PlayerInputUsecase,
    private val mediaPlayerController: MediaPlayerController,
    private val trackList: List<Item>,
) : InstanceKeeper.Instance {
    private val viewModelScope = CoroutineScope(Dispatchers.Unconfined)
    val chartDetailsViewState = MutableStateFlow(
        PlayerViewState(
            trackList = trackList, mediaPlayerController = mediaPlayerController
        )
    )

    init {
        viewModelScope.launch {
            playerInputUsecase.chatDetailsInput.collectLatest {
                when (it) {
                    is PlayerComponent.Input.PlayTrack -> chartDetailsViewState.value =
                        chartDetailsViewState.value.copy(playingTrackId = it.trackId)

                    is PlayerComponent.Input.UpdateTracks -> chartDetailsViewState.value =
                        chartDetailsViewState.value.copy(trackList = it.tracksList)
                }
            }
        }
    }

    override fun onDestroy() {
        viewModelScope.cancel()
    }
}