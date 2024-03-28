package musicapp_kmp.network.models.topfiftycharts


import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Album(
    @SerialName("album_type")
    val albumType: String?,
    @SerialName("artists")
    val artists: List<ArtistX>?,
    @SerialName("available_markets")
    val availableMarkets: List<String>?,
    @SerialName("external_urls")
    val externalUrls: ExternalUrls?,
    @SerialName("href")
    val href: String?,
    @SerialName("id")
    val id: String?,
    @SerialName("images")
    val images: List<ImageX>?,
    @SerialName("name")
    val name: String?,
    @SerialName("release_date")
    val releaseDate: String?,
    @SerialName("release_date_precision")
    val releaseDatePrecision: String?,
    @SerialName("total_tracks")
    val totalTracks: Int?,
    @SerialName("type")
    val type: String?,
    @SerialName("uri")
    val uri: String?
) : Parcelable