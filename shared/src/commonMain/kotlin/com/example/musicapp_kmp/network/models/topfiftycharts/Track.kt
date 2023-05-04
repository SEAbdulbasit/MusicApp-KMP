package com.example.musicapp_kmp.network.models.topfiftycharts


import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Track(
    @SerialName("album")
    val album: Album?,
    @SerialName("artists")
    val artists: List<ArtistX>?,
    @SerialName("available_markets")
    val availableMarkets: List<String>?,
    @SerialName("disc_number")
    val discNumber: Int?,
    @SerialName("duration_ms")
    val durationMs: Int?,
    @SerialName("episode")
    val episode: Boolean?,
    @SerialName("explicit")
    val explicit: Boolean?,
    @SerialName("external_ids")
    val externalIds: ExternalIds?,
    @SerialName("external_urls")
    val externalUrls: ExternalUrls?,
    @SerialName("href")
    val href: String?,
    @SerialName("id")
    val id: String?,
    @SerialName("is_local")
    val isLocal: Boolean?,
    @SerialName("name")
    val name: String?,
    @SerialName("popularity")
    val popularity: Int?,
    @SerialName("preview_url")
    val previewUrl: String?,
    @SerialName("track")
    val track: Boolean?,
    @SerialName("track_number")
    val trackNumber: Int?,
    @SerialName("type")
    val type: String?,
    @SerialName("uri")
    val uri: String?
) : Parcelable