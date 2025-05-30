package musicapp.player


interface MediaPlayerListener {

    fun onReady()
    fun onAudioCompleted()
    fun onError()
    fun onTrackChanged(trackId: String)
    fun onBufferingStateChanged(isBuffering: Boolean) { /* Optional implementation */
    }

    fun onPlaybackStateChanged(isPlaying: Boolean) { /* Optional implementation */
    }
}
