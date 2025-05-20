package musicapp.player

import musicapp.network.models.topfiftycharts.Track

data class MediaItem(
    val title: String,
    val artist: String,
    val artworkUrl: String?,
    val pathSource: String
)

fun Track.toMediaItem(): MediaItem {
    return MediaItem(
        title = name.toString(),
        artist = artists?.map { it.name }?.joinToString(",").toString(),
        artworkUrl = album?.images?.first()?.url.orEmpty(),
        pathSource = previewUrl.toString()
    )
}