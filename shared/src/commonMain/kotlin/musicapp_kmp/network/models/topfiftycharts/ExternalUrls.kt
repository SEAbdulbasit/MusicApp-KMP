package musicapp_kmp.network.models.topfiftycharts

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExternalUrls(
    @SerialName("spotify")
    val spotify: String?
)