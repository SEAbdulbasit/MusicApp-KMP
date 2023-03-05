package com.example.musicapp_kmp.player

import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent
import java.util.*

actual class MediaPlayerController {
    private var mediaPlayer: MediaPlayer? = null
    private var listener: MediaPlayerListener? = null

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
                listener?.onVideoCompleted()
            }

            override fun error(mediaPlayer: MediaPlayer?) {
                super.error(mediaPlayer)
                listener?.onError()
            }
        })

    }

    actual fun prepare(
        pathSource: String, listener: MediaPlayerListener
    ) {

        if (mediaPlayer == null) {
            initMediaPlayer()
            this.listener = listener
        }

        if (mediaPlayer?.status()?.isPlaying == true) {
            mediaPlayer?.controls()?.stop()
        }


        mediaPlayer?.media()?.play(pathSource)
    }

    actual fun start() {
        mediaPlayer?.controls()?.start()
    }

    actual fun pause() {
        mediaPlayer?.controls()?.pause()
    }

    actual fun stop() {
        mediaPlayer?.controls()?.stop()
    }

    actual fun isPlaying(): Boolean {
        return mediaPlayer?.status()?.isPlaying ?: false
    }

    actual fun release() {
        mediaPlayer?.release()
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
}