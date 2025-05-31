package musicapp.player

import kotlinx.coroutines.*
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent
import java.util.*
import java.util.logging.Logger

actual class MediaPlayerController actual constructor(val platformContext: musicapp.utils.PlatformContext) {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var mediaPlayer: MediaPlayer? = null
    private var listener: MediaPlayerListener? = null
    private var currentTrack: TrackItem? = null
    private var trackList: List<TrackItem> = emptyList()
    private var currentTrackIndex: Int = -1
    private val logger = Logger.getLogger(MediaPlayerController::class.java.name)

    init {
        System.setProperty("vlcj.log", "DEBUG")
    }

    private fun initMediaPlayer(): Boolean {
        try {
            NativeDiscovery().discover()
            releaseMediaPlayer()

            val component = if (isMacOS()) CallbackMediaPlayerComponent() else EmbeddedMediaPlayerComponent()
            mediaPlayer = component.mediaPlayerFactory().mediaPlayers().newMediaPlayer()

            mediaPlayer?.events()?.addMediaPlayerEventListener(object : MediaPlayerEventAdapter() {
                override fun mediaPlayerReady(mediaPlayer: MediaPlayer?) {
                    scope.launch { listener?.onReady() }
                }

                override fun finished(mediaPlayer: MediaPlayer?) {
                    scope.launch {
                        if (!playNextTrack()) {
                            listener?.onAudioCompleted()
                        }
                    }
                }

                override fun error(mediaPlayer: MediaPlayer?) {
                    scope.launch { listener?.onError() }
                }

                override fun playing(mediaPlayer: MediaPlayer?) {
                    scope.launch { listener?.onPlaybackStateChanged(true) }
                }

                override fun paused(mediaPlayer: MediaPlayer?) {
                    scope.launch { listener?.onPlaybackStateChanged(false) }
                }

                override fun buffering(mediaPlayer: MediaPlayer?, newCache: Float) {
                    scope.launch { listener?.onBufferingStateChanged(newCache < 100f) }
                }
            })

            return true
        } catch (e: Exception) {
            logger.severe("Failed to initialize media player: ${e.message}")
            listener?.onError()
            return false
        }
    }

    private fun releaseMediaPlayer() {
        try {
            mediaPlayer?.controls()?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (e: Exception) {
            logger.severe("Error releasing media player: ${e.message}")
        }
    }

    actual fun prepare(mediaItem: TrackItem, listener: MediaPlayerListener) {
        this.listener = listener
        this.currentTrack = mediaItem

        if (mediaItem.pathSource.isNullOrBlank()) {
            listener.onError()
            return
        }

        scope.launch {
            try {
                // Initialize player if needed
                if (mediaPlayer == null) {
                    if (!initMediaPlayer()) return@launch
                }

                // Update track index if in playlist
                if (trackList.isNotEmpty()) {
                    trackList.indexOfFirst { it.id == mediaItem.id }
                        .takeIf { it >= 0 }
                        ?.let { currentTrackIndex = it }
                }

                // Prepare and play media
                mediaPlayer?.media()?.prepare(mediaItem.pathSource)
                mediaPlayer?.controls()?.play()
                listener.onBufferingStateChanged(true)
            } catch (e: Exception) {
                // Try to recover once
                try {
                    if (initMediaPlayer()) {
                        mediaPlayer?.media()?.prepare(mediaItem.pathSource)
                        mediaPlayer?.controls()?.play()
                        listener.onBufferingStateChanged(true)
                    } else {
                        listener.onError()
                    }
                } catch (e: Exception) {
                    listener.onError()
                }
            }
        }
    }

    private fun playTrackAt(index: Int): Boolean {
        if (index < 0 || index >= trackList.size || listener == null) return false

        currentTrackIndex = index
        val track = trackList[index]
        listener?.onTrackChanged(track.id)

        try {
            prepare(track, listener!!)
            return true
        } catch (e: Exception) {
            listener?.onError()
            return false
        }
    }

    actual fun playNextTrack(): Boolean {
        if (trackList.isEmpty() || currentTrackIndex < 0) return false

        val nextIndex = currentTrackIndex + 1
        if (nextIndex >= trackList.size) return false

        return playTrackAt(nextIndex)
    }

    actual fun playPreviousTrack(): Boolean {
        if (trackList.isEmpty() || currentTrackIndex <= 0) return false

        return playTrackAt(currentTrackIndex - 1)
    }

    fun release() {
        releaseMediaPlayer()
        scope.cancel()
    }

    private fun isMacOS(): Boolean {
        val os = System.getProperty("os.name", "generic").lowercase(Locale.ENGLISH)
        return os.contains("mac") || os.contains("darwin")
    }

    actual fun setTrackList(trackList: List<TrackItem>, currentTrackId: String) {
        this.trackList = trackList
        this.currentTrackIndex = trackList.indexOfFirst { it.id == currentTrackId }.takeIf { it >= 0 } ?: 0
    }

    actual fun getCurrentTrack(): TrackItem? {
        return currentTrack ?: trackList.getOrNull(currentTrackIndex)
    }

    actual fun start() {
        mediaPlayer?.controls()?.play()
        listener?.onPlaybackStateChanged(true)
    }

    actual fun pause() {
        mediaPlayer?.controls()?.pause()
        listener?.onPlaybackStateChanged(false)
    }

    actual fun getCurrentPosition(): Long? {
        return mediaPlayer?.status()?.time()?.toLong() ?: 0L
    }

    actual fun getDuration(): Long? {
        return mediaPlayer?.status()?.length() ?: 0L
    }

    actual fun seekTo(seconds: Long) {
        mediaPlayer?.controls()?.setTime(seconds)
    }

    actual fun isPlaying(): Boolean {
        return mediaPlayer?.status()?.isPlaying ?: false
    }
}