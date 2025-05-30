package musicapp.player

import kotlinx.browser.document
import musicapp.utils.PlatformContext
import org.w3c.dom.HTMLAudioElement

actual class MediaPlayerController actual constructor(val platformContext: PlatformContext) {
    private val audioElement = document.createElement("audio") as HTMLAudioElement
    private var listener: MediaPlayerListener? = null
    private var currentTrack: TrackItem? = null

    private var trackList: List<TrackItem> = emptyList()
    private var currentTrackIndex: Int = -1

    actual fun prepare(
        mediaItem: TrackItem,
        listener: MediaPlayerListener
    ) {
        this.listener = listener
        this.currentTrack = mediaItem

        if (trackList.isNotEmpty()) {
            val index = trackList.indexOfFirst { it.id == mediaItem.id }
            if (index >= 0) {
                currentTrackIndex = index
            }
        }

        listener.onBufferingStateChanged(true)

        audioElement.src = mediaItem.pathSource
        audioElement.addEventListener("canplaythrough", {
            // Audio is ready to play without interruption
            listener.onBufferingStateChanged(false)
            listener.onReady()
            audioElement.play()
            listener.onPlaybackStateChanged(true)
        })

        audioElement.onended = {
            val nextTrackPlayed = playNextTrack()
            if (!nextTrackPlayed) {
                listener.onAudioCompleted()
            }
        }
        audioElement.addEventListener("error", {
            listener.onError()
        })

    }

    actual fun start() {
        audioElement.play()
        listener?.onPlaybackStateChanged(true)
    }

    actual fun pause() {
        audioElement.pause()
        listener?.onPlaybackStateChanged(false)
    }

    actual fun seekTo(seconds: Long) {
        audioElement.currentTime = seconds / 1000.0
    }

    actual fun getCurrentPosition(): Long? {
        return (audioElement.currentTime * 1000).toLong()
    }

    actual fun getDuration(): Long? {
        return (audioElement.duration * 1000).toLong()
    }

    actual fun isPlaying(): Boolean {
        return !audioElement.paused
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
