package musicapp.player

import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Player.STATE_ENDED
import androidx.media3.common.Player.STATE_READY
import androidx.media3.exoplayer.ExoPlayer

actual class MediaPlayerController actual constructor(platformContext: PlatformContext) {
    val player = ExoPlayer.Builder(platformContext.applicationContext).build()

    actual fun prepare(pathSource: String, listener: MediaPlayerListener) {
        val mediaItem = MediaItem.fromUri(pathSource)
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
        player.setMediaItem(mediaItem)
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

