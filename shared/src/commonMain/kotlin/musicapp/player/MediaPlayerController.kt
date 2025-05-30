package musicapp.player

import musicapp.utils.PlatformContext

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class MediaPlayerController(platformContext: PlatformContext) {
    fun prepare(
        mediaItem: TrackItem,
        listener: MediaPlayerListener
    )

    fun setTrackList(trackList: List<TrackItem>, currentTrackId: String)

    fun playNextTrack(): Boolean

    fun playPreviousTrack(): Boolean

    fun start()

    fun pause()

    fun getCurrentPosition(): Long?

    fun getDuration(): Long?

    fun seekTo(seconds: Long)

    fun isPlaying(): Boolean

    fun getCurrentTrack(): TrackItem?
}
