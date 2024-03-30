package musicapp_kmp.network.models.topfiftycharts


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddedBy(
    @SerialName("external_urls")
    val externalUrls: ExternalUrls?,
    @SerialName("href")
    val href: String?,
    @SerialName("id")
    val id: String?,
    @SerialName("type")
    val type: String?,
    @SerialName("uri")
    val uri: String?
)