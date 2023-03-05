package com.example.musicapp_kmp.player

actual class MediaPlayerController {
    actual fun prepare(
        pathSource: String,
        listener: MediaPlayerListener
    ) {
    }

    actual fun start() {
    }

    actual fun pause() {
    }

    actual fun stop() {
    }

    actual fun isPlaying(): Boolean {
        return false
    }

    actual fun release() {
    }

}