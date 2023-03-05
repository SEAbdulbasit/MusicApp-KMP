package com.example.musicapp_kmp.player

import kotlinx.browser.document
import org.w3c.dom.HTMLAudioElement

actual class MediaPlayerController {
    private val audioElement = document.createElement("audio") as HTMLAudioElement

    actual fun prepare(
        pathSource: String,
        listener: MediaPlayerListener
    ) {
        audioElement.src = pathSource
        audioElement.addEventListener("canplaythrough", {
            // Audio is ready to play without interruption
            listener.onReady()
        })

        audioElement.onended = {
            listener.onVideoCompleted()
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

    actual fun stop() {
    }

    actual fun isPlaying(): Boolean {
        return !audioElement.paused
    }

    actual fun release() {
    }

}
