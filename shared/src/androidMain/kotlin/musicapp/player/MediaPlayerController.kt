package musicapp.player

import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Player.STATE_ENDED
import androidx.media3.common.Player.STATE_READY
import androidx.media3.exoplayer.ExoPlayer
import musicapp.player.mapper.toMediaItem
import musicapp.player.service.MediaService

actual class MediaPlayerController actual constructor(private val platformContext: PlatformContext) {
    private val player = PlayerServiceLocator.exoPlayer

    init {
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying)
                    startMediaServiceIfNeeded()
            }
        })
    }

    private fun startMediaServiceIfNeeded() {
        if (MediaService.isRunning) return
        val intent = Intent(platformContext.applicationContext, MediaService::class.java)
        ContextCompat.startForegroundService(platformContext.applicationContext, intent)
    }

    actual fun prepare(
        mediaItem: musicapp.player.MediaItem,
        listener: MediaPlayerListener
    ) {
        player.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                listener.onError()
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                when (playbackState) {
                    STATE_READY -> listener.onReady()
                    STATE_ENDED -> listener.onAudioCompleted()
                }
            }

            override fun onPlayerErrorChanged(error: PlaybackException?) {
                super.onPlayerErrorChanged(error)
                listener.onError()
            }
        })
        player.setMediaItem(mediaItem.toMediaItem())
        player.prepare()
        player.play()
    }

    actual fun start() {
        player.play()
    }

    actual fun pause() {
        if (player.isPlaying)
            player.pause()
    }

    actual fun seekTo(seconds: Long) {
        if (player.isPlaying)
            player.seekTo(seconds)
    }

    actual fun getCurrentPosition(): Long? {
        return player.currentPosition
    }

    actual fun getDuration(): Long? {
        return player.duration
    }

    actual fun stop() {
        player.stop()
    }

    actual fun release() {
        player.release()
    }

    actual fun isPlaying(): Boolean {
        return player.isPlaying
    }
}

