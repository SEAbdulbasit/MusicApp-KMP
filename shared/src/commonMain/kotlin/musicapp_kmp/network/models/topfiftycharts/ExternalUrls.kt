package musicapp_kmp.network.models.topfiftycharts


import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class ExternalUrls(
    @SerialName("spotify")
    val spotify: String?
) : Parcelable