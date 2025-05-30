package musicapp.playerview

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import musicapp.SEEK_TO_SECONDS
import musicapp.decompose.PlayerComponent
import musicapp.player.MediaPlayerController
import musicapp.player.MediaPlayerListener
import musicapp.player.TrackItem


/**
 * Created by abdulbasit on 26/02/2023.
 */
class PlayerViewModel(
    private val mediaPlayerController: MediaPlayerController,
    trackList: List<TrackItem>,
    selectedTrack: String,
    playerInputs: SharedFlow<PlayerComponent.Input>
) : InstanceKeeper.Instance {

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        exception.printStackTrace()
    }

    private val job = SupervisorJob()
    private val viewModelScope = CoroutineScope(Dispatchers.Main + coroutineExceptionHandler + job)

    val playerViewState = MutableStateFlow(
        PlayerViewState(
            trackList = trackList,
            playingTrackId = selectedTrack,
            isPlaying = false
        )
    )

    private val mediaPlayerListener = object : MediaPlayerListener {
        override fun onReady() {
            updatePlayerState {
                it.copy(
                    isBuffering = false,
                    errorState = false
                )
            }
        }

        override fun onAudioCompleted() {
            updatePlayerState {
                it.copy(
                    isPlaying = false
                )
            }
        }

        override fun onError() {
            updatePlayerState {
                it.copy(
                    errorState = true,
                    isBuffering = false
                )
            }
        }

        override fun onTrackChanged(trackId: String) {
            updatePlayerState {
                it.copy(
                    playingTrackId = trackId,
                    errorState = false
                )
            }
        }

        override fun onBufferingStateChanged(isBuffering: Boolean) {
            updatePlayerState {
                it.copy(
                    isBuffering = isBuffering
                )
            }
        }

        override fun onPlaybackStateChanged(isPlaying: Boolean) {
            updatePlayerState {
                it.copy(
                    isPlaying = isPlaying
                )
            }
        }
    }

    private fun updatePlayerState() {
        val currentTrack = mediaPlayerController.getCurrentTrack() ?: return
        val currentPosition = mediaPlayerController.getCurrentPosition() ?: 0L
        val duration = mediaPlayerController.getDuration()
        val isPlaying = mediaPlayerController.isPlaying()

        val newState = playerViewState.value.copy(
            playingTrackId = currentTrack.id,
            currentPosition = currentPosition,
            duration = duration,
            isPlaying = isPlaying
        )
        playerViewState.value = newState
    }

    private fun updatePlayerState(transform: (PlayerViewState) -> PlayerViewState) {
        playerViewState.value = transform(playerViewState.value)
    }

    fun syncWithMediaPlayer() {
        updatePlayerState()
    }

    fun playTrack(trackId: String) {
        val track = playerViewState.value.trackList.find { it.id == trackId } ?: return
        mediaPlayerController.prepare(track, mediaPlayerListener)
    }

    fun togglePlayPause() {
        if (mediaPlayerController.isPlaying()) {
            mediaPlayerController.pause()
        } else {
            mediaPlayerController.start()
        }
        updatePlayerState()
    }

    fun playNextTrack() {
        if (mediaPlayerController.playNextTrack()) {
            updatePlayerState()
        }
    }

    fun playPreviousTrack() {
        if (mediaPlayerController.playPreviousTrack()) {
            updatePlayerState()
        }
    }

    fun getCurrentTrackIndex(): Int {
        val currentTrackId = playerViewState.value.playingTrackId
        return playerViewState.value.trackList.indexOfFirst { it.id == currentTrackId }
    }

    init {
        viewModelScope.launch {
            playerInputs.collectLatest {
                when (it) {
                    is PlayerComponent.Input.PlayTrack -> {
                        val newState = playerViewState.value.copy(
                            playingTrackId = it.trackId,
                            trackList = it.tracksList
                        )
                        playerViewState.value = newState

                        mediaPlayerController.setTrackList(it.tracksList, it.trackId)
                        playTrack(it.trackId)
                    }

                    is PlayerComponent.Input.UpdateTracks -> {
                        val newState = playerViewState.value.copy(trackList = it.tracksList)
                        playerViewState.value = newState

                        mediaPlayerController.setTrackList(it.tracksList, newState.playingTrackId)
                    }
                }
            }
        }

        mediaPlayerController.setTrackList(trackList, selectedTrack)
    }

    fun rewind5Seconds() {
        mediaPlayerController.getCurrentPosition()?.let {
            (it - SEEK_TO_SECONDS).coerceAtLeast(0).let(mediaPlayerController::seekTo)
        }
        updatePlayerState()
    }

    fun forward5Seconds() {
        mediaPlayerController.getCurrentPosition()?.let { currentPosition ->
            mediaPlayerController.getDuration()?.let { duration ->
                (currentPosition + SEEK_TO_SECONDS).coerceAtMost(duration)
                    .let(mediaPlayerController::seekTo)
            }
        }
        updatePlayerState()
    }

    fun setBuffering(isBuffering: Boolean) {
        val newState = playerViewState.value.copy(
            isBuffering = isBuffering
        )
        playerViewState.value = newState
    }

    override fun onDestroy() {
        viewModelScope.cancel()
    }
}
