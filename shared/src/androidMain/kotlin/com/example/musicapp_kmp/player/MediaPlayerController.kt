package com.example.musicapp_kmp.player

import android.media.MediaPlayer
import android.net.Uri

actual class MediaPlayerController {
    private var mediaPlayer: MediaPlayer? = null
    private var uri: Uri? = null

    private var isSurfaceReady = false
        set(value) {
            field = value
            if (value.and(isMediaPlayerReady)) onReady()
        }
    private var isMediaPlayerReady = false
        set(value) {
            field = value
            if (value.and(isSurfaceReady)) onReady()
        }

    private lateinit var listener: MediaPlayerListener

    private fun onReady() {
        listener.onReady()
    }

    actual fun prepare(pathSource: String, listener: MediaPlayerListener) {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        }
        this.listener = listener
        this.uri = Uri.parse(pathSource)
        mediaPlayer = MediaPlayer().apply {
            setOnCompletionListener { this@MediaPlayerController.listener.onVideoCompleted() }
            setOnPreparedListener { this@MediaPlayerController.listener.onReady() }
            setOnErrorListener { mediaPlayer, i, i2 ->
                this@MediaPlayerController.listener.onError()
                mediaPlayer.release()
                true
            }
        }
        mediaPlayer?.setDataSource(pathSource)
        mediaPlayer?.prepareAsync()

    }

    actual fun start() {
        mediaPlayer?.start()
    }

    actual fun pause() {
        mediaPlayer?.also { mediaPlayer ->
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
            }
        }
    }

    actual fun stop() {
        mediaPlayer?.stop()
    }

    actual fun release() {
        isMediaPlayerReady = false
        mediaPlayer?.release()
        mediaPlayer = null
    }

    actual fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }
}

