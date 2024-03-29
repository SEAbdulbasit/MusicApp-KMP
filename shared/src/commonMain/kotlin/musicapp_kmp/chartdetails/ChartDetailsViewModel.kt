package musicapp_kmp.chartdetails

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import musicapp_kmp.decompose.ChartDetailsComponent
import musicapp_kmp.network.SpotifyApi


/**
 * Created by abdulbasit on 26/02/2023.
 */
class ChartDetailsViewModel(
    private val chartDetailsFlow: SharedFlow<ChartDetailsComponent.Input>,
    api: SpotifyApi,
    playlistId: String,
    playingTrackId: String,
) : InstanceKeeper.Instance {

    private val viewModelScope = CoroutineScope(Dispatchers.Unconfined)
    val chartDetailsViewState =
        MutableStateFlow<ChartDetailsViewState>(ChartDetailsViewState.Loading)

    init {
        viewModelScope.launch {
            launch {
                try {
                    val playlist = api.getPlayList(playlistId)
                    chartDetailsViewState.value = ChartDetailsViewState.Success(
                        chartDetails = playlist,
                        playingTrackId = playingTrackId
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    chartDetailsViewState.value =
                        ChartDetailsViewState.Failure(e.message.toString())
                }
            }
            launch {
                chartDetailsFlow.collectLatest {
                    when (it) {
                        is ChartDetailsComponent.Input.TrackUpdated ->
                            when (val state = chartDetailsViewState.value) {
                                is ChartDetailsViewState.Success -> {
                                    chartDetailsViewState.emit(state.copy(playingTrackId = it.trackId))
                                }
                                else -> {}
                            }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        viewModelScope.cancel()
    }
}