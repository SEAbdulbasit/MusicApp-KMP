package musicapp.player

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class MediaPlayerController(platformContext: PlatformContext) {
    fun prepare(mediaItem: MediaItem, listener: MediaPlayerListener)

    fun start()

    fun pause()

    fun stop()

    fun getCurrentPosition(): Long?

    fun getDuration(): Long?

    fun seekTo(seconds: Long)

    fun isPlaying(): Boolean

    fun release()
}

expect class PlatformContext