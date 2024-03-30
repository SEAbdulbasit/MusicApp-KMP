package musicapp.network.models.newreleases


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExternalUrlsX(
    @SerialName("spotify")
    val spotify: String?
)