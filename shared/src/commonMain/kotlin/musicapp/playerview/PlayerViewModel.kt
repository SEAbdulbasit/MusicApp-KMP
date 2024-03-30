package musicapp.playerview

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import musicapp.decompose.PlayerComponent
import musicapp.network.models.topfiftycharts.Item
import musicapp.player.MediaPlayerController
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


/**
 * Created by abdulbasit on 26/02/2023.
 */
class PlayerViewModel(
    mediaPlayerController: MediaPlayerController,
    trackList: List<Item>,
    playerInputs: SharedFlow<PlayerComponent.Input>
) : InstanceKeeper.Instance {

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        exception.printStackTrace()
    }

    private val job = SupervisorJob()
    private val viewModelScope = CoroutineScope(Dispatchers.Main + coroutineExceptionHandler + job)

    val chartDetailsViewState = MutableStateFlow(
        PlayerViewState(
            trackList = trackList,
            mediaPlayerController = mediaPlayerController
        )
    )

    init {
        viewModelScope.launch {
            playerInputs.collectLatest {
                when (it) {
                    is PlayerComponent.Input.PlayTrack ->
                        chartDetailsViewState.value =
                            chartDetailsViewState.value.copy(playingTrackId = it.trackId, trackList = it.tracksList)

                    is PlayerComponent.Input.UpdateTracks ->
                        chartDetailsViewState.value =
                            chartDetailsViewState.value.copy(trackList = it.tracksList)
                }
            }
        }
    }

    override fun onDestroy() {
        viewModelScope.cancel()
    }
}