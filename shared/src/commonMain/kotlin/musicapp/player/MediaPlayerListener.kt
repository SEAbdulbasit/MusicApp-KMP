package musicapp.player

interface MediaPlayerListener {
    fun onReady()
    fun onAudioCompleted()
    fun onError()
}
