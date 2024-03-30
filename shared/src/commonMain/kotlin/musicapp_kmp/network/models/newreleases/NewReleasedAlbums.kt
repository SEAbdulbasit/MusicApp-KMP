package musicapp_kmp.network.models.newreleases


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import musicapp_kmp.network.models.newreleases.Albums

@Serializable
data class NewReleasedAlbums(
    @SerialName("albums")
    val albums: Albums?
)