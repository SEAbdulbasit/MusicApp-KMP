package musicapp.player

import kotlinx.serialization.Serializable
import musicapp.network.models.topfiftycharts.Track

@Serializable
data class TrackItem(
    val id: String,
    val title: String,
    val artist: String,
    val albumImageUrl: String,
    val pathSource: String
)

fun Track.toMediaItem(): TrackItem {
    return TrackItem(
        id = id ?: "",
        title = name.toString(),
        artist = artists?.joinToString(",") { it.name ?: "" }.toString(),
        albumImageUrl = album?.images?.first()?.url.orEmpty(),
        pathSource = previewUrl.toString()
    )
}