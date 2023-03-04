package com.example.musicapp_kmp.player

expect class MediaPlayerController {
    fun prepare(pathSource: String, listener: MediaPlayerListener)

    fun start()

    fun pause()

    fun stop()

    fun isPlaying(): Boolean

    fun release()
}
