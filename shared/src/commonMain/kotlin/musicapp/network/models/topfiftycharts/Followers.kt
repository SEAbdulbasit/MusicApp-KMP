package musicapp.network.models.topfiftycharts


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Followers(
    @SerialName("href")
    val href: String?,
    @SerialName("total")
    val total: Int?
)