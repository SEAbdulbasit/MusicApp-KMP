package musicapp_kmp.network.models.topfiftycharts


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Item(
    @SerialName("added_at")
    val addedAt: String?,
    @SerialName("added_by")
    val addedBy: AddedBy?,
    @SerialName("is_local")
    val isLocal: Boolean?,
    @SerialName("primary_color")
    val primaryColor: String?,
    @SerialName("track")
    val track: Track?,
    @SerialName("video_thumbnail")
    val videoThumbnail: VideoThumbnail?
)