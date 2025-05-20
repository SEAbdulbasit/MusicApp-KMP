package musicapp.player

import kotlinx.browser.document
import org.w3c.dom.HTMLAudioElement

actual class MediaPlayerController actual constructor(val platformContext: PlatformContext) {
    private val audioElement = document.createElement("audio") as HTMLAudioElement

    actual fun prepare(
        mediaItem: MediaItem,
        listener: MediaPlayerListener
    ) {
        audioElement.src = mediaItem.pathSource
        audioElement.addEventListener("canplaythrough", {
            // Audio is ready to play without interruption
            listener.onReady()
            audioElement.play()
        })

        audioElement.onended = {
            listener.onAudioCompleted()
        }
        audioElement.addEventListener("error", {
            listener.onError()
        })

    }

    actual fun start() {
        audioElement.play()
    }

    actual fun pause() {
        audioElement.pause()
    }

    actual fun seekTo(seconds: Long) {
        audioElement.fastSeek(seconds.toDouble())
    }

    actual fun getCurrentPosition(): Long? {
        return (audioElement.currentTime * 1000).toLong()
    }

    actual fun getDuration(): Long? {
        return (audioElement.duration * 1000).toLong()
    }

    actual fun stop() {
    }

    actual fun isPlaying(): Boolean {
        return !audioElement.paused
    }

    actual fun release() {
    }

}
