package musicapp.player

import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent
import java.util.*

actual class MediaPlayerController actual constructor(val platformContext: musicapp.utils.PlatformContext) {

    private var mediaPlayer: MediaPlayer? = null
    private var listener: MediaPlayerListener? = null
    private var currentTrack: TrackItem? = null

    private var trackList: List<TrackItem> = emptyList()
    private var currentTrackIndex: Int = -1

    private fun initMediaPlayer() {
        NativeDiscovery().discover()

        mediaPlayer =
                // see https://github.com/caprica/vlcj/issues/887#issuecomment-503288294 for why we're using CallbackMediaPlayerComponent for macOS.
            if (isMacOS()) {
                CallbackMediaPlayerComponent()
            } else {
                EmbeddedMediaPlayerComponent()
            }.mediaPlayer()

        mediaPlayer?.events()?.addMediaPlayerEventListener(object : MediaPlayerEventAdapter() {
            override fun mediaPlayerReady(mediaPlayer: MediaPlayer?) {
                super.mediaPlayerReady(mediaPlayer)
                listener?.onReady()
            }

            override fun finished(mediaPlayer: MediaPlayer?) {
                super.finished(mediaPlayer)
                listener?.onAudioCompleted()
            }

            override fun error(mediaPlayer: MediaPlayer?) {
                super.error(mediaPlayer)
                listener?.onError()
            }
        })

    }

    actual fun prepare(
        mediaItem: TrackItem,
        listener: MediaPlayerListener
    ) {
        if (mediaPlayer == null) {
            initMediaPlayer()
            this.listener = listener
        }

        if (mediaPlayer?.status()?.isPlaying == true) {
            mediaPlayer?.controls()?.stop()
        }


        mediaPlayer?.media()?.play(mediaItem.pathSource)
    }

    actual fun start() {
        mediaPlayer?.controls()?.start()
    }

    actual fun pause() {
        mediaPlayer?.controls()?.pause()
    }

    actual fun seekTo(seconds: Long) {
        mediaPlayer?.controls()?.setTime(seconds)
    }

    actual fun getCurrentPosition(): Long? {
        return mediaPlayer?.status()?.time()
    }

    actual fun getDuration(): Long? {
        return mediaPlayer?.media()?.info()?.duration()
    }

    actual fun isPlaying(): Boolean {
        return mediaPlayer?.status()?.isPlaying ?: false
    }

    private fun Any.mediaPlayer(): MediaPlayer {
        return when (this) {
            is CallbackMediaPlayerComponent -> mediaPlayer()
            is EmbeddedMediaPlayerComponent -> mediaPlayer()
            else -> throw IllegalArgumentException("You can only call mediaPlayer() on vlcj player component")
        }
    }

    private fun isMacOS(): Boolean {
        val os = System.getProperty("os.name", "generic").lowercase(Locale.ENGLISH)
        return os.indexOf("mac") >= 0 || os.indexOf("darwin") >= 0
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

        listener?.onTrackChanged(nextTrack.id)

        prepare(nextTrack, listener ?: return false)
        return true
    }

    actual fun playPreviousTrack(): Boolean {
        if (trackList.isEmpty() || currentTrackIndex <= 0) {
            return false
        }

        val previousIndex = currentTrackIndex - 1
        currentTrackIndex = previousIndex
        val previousTrack = trackList[previousIndex]

        listener?.onTrackChanged(previousTrack.id)

        prepare(previousTrack, listener ?: return false)
        return true
    }

    actual fun getCurrentTrack(): TrackItem? {
        currentTrack?.let { return it }

        if (trackList.isEmpty() || currentTrackIndex < 0 || currentTrackIndex >= trackList.size) {
            return null
        }
        return trackList[currentTrackIndex]
    }
}