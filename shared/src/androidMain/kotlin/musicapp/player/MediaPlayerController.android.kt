package musicapp.player

import android.annotation.SuppressLint
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Player.STATE_ENDED
import androidx.media3.common.Player.STATE_READY
import musicapp.player.mapper.toMediaItem
import musicapp.player.service.MediaService
import musicapp.utils.PlatformContext

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class MediaPlayerController actual constructor(val platformContext: PlatformContext) {
    private val player = PlayerServiceLocator.exoPlayer

    private var trackList: List<TrackItem> = emptyList()
    private var currentTrackIndex: Int = -1
    private var currentListener: MediaPlayerListener? = null

    init {
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying)
                    startMediaServiceIfNeeded()
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO) {
                    playNextTrack()
                }
            }
        })
    }

    actual fun setTrackList(trackList: List<TrackItem>, currentTrackId: String) {
        this.trackList = trackList
        this.currentTrackIndex = trackList.indexOfFirst { it.id == currentTrackId }.takeIf { it >= 0 } ?: 0
    }

    actual fun playNextTrack(): Boolean {
        if (trackList.isEmpty() || currentTrackIndex < 0) {
            return false
        }

        val nextIndex = currentTrackIndex + 1
        if (nextIndex >= trackList.size) {
            return false
        }

        currentTrackIndex = nextIndex
        val nextTrack = trackList[nextIndex]

        currentListener?.onTrackChanged(nextTrack.id)

        prepare(nextTrack, currentListener ?: return false)
        updateNotification()
        return true
    }

    actual fun playPreviousTrack(): Boolean {
        if (trackList.isEmpty() || currentTrackIndex <= 0) {
            return false
        }

        val previousIndex = currentTrackIndex - 1
        currentTrackIndex = previousIndex
        val previousTrack = trackList[previousIndex]

        currentListener?.onTrackChanged(previousTrack.id)

        prepare(previousTrack, currentListener ?: return false)
        updateNotification()
        return true
    }

    actual fun getCurrentTrack(): TrackItem? {
        if (trackList.isEmpty() || currentTrackIndex < 0 || currentTrackIndex >= trackList.size) {
            return null
        }
        return trackList[currentTrackIndex]
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun startMediaServiceIfNeeded() {
        if (MediaService.isRunning) return
        val intent = Intent(platformContext.applicationContext, MediaService::class.java)
        ContextCompat.startForegroundService(platformContext.applicationContext, intent)
    }

    private fun updateNotification() {
        if (MediaService.isRunning) {
            // Send an intent to the service to update the notification without stopping/restarting
            val intent = Intent(platformContext.applicationContext, MediaService::class.java)
            intent.action = MediaService.ACTION_UPDATE_NOTIFICATION
            ContextCompat.startForegroundService(platformContext.applicationContext, intent)
        } else {
            // If service is not running, start it
            startMediaServiceIfNeeded()
        }
    }

    private var playerListener: Player.Listener? = null

    actual fun prepare(mediaItem: TrackItem, listener: MediaPlayerListener) {
        this.currentListener = listener

        if (trackList.isNotEmpty()) {
            val index = trackList.indexOfFirst { it.id == mediaItem.id }
            if (index >= 0) {
                currentTrackIndex = index
            }
        }

        playerListener?.let { player.removeListener(it) }
        playerListener = object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                listener.onError()
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                when (playbackState) {
                    STATE_READY -> {
                        listener.onReady()
                        listener.onBufferingStateChanged(false)
                    }

                    STATE_ENDED -> {
                        val nextTrackPlayed = playNextTrack()

                        if (!nextTrackPlayed) {
                            listener.onAudioCompleted()
                        }
                    }
                }
            }

            override fun onPlayerErrorChanged(error: PlaybackException?) {
                super.onPlayerErrorChanged(error)
                listener.onError()
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                listener.onPlaybackStateChanged(isPlaying)

                if (isPlaying) {
                    startMediaServiceIfNeeded()
                }
            }

            override fun onIsLoadingChanged(isLoading: Boolean) {
                super.onIsLoadingChanged(isLoading)
                listener.onBufferingStateChanged(isLoading)
            }
        }

        listener.onBufferingStateChanged(true)

        player.addListener(playerListener!!)
        player.setMediaItem(mediaItem.toMediaItem())
        player.prepare()
        player.play()

        // Ensure notification is updated with new track info
        startMediaServiceIfNeeded()
        updateNotification()
    }

    actual fun start() {
        player.play()
    }

    actual fun pause() {
        if (player.isPlaying)
            player.pause()
    }

    actual fun seekTo(seconds: Long) {
        player.seekTo(seconds)
    }

    actual fun getCurrentPosition(): Long? {
        return player.currentPosition
    }

    actual fun getDuration(): Long? {
        return player.duration
    }

    actual fun isPlaying(): Boolean {
        return player.isPlaying
    }
}
