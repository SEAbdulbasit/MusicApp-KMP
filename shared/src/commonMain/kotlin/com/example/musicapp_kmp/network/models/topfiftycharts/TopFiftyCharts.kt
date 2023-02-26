package com.example.musicapp_kmp.network.models.topfiftycharts


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TopFiftyCharts(
    @SerialName("collaborative")
    val collaborative: Boolean?,
    @SerialName("description")
    val description: String?,
    @SerialName("external_urls")
    val externalUrls: ExternalUrls?,
    @SerialName("followers")
    val followers: Followers?,
    @SerialName("href")
    val href: String?,
    @SerialName("id")
    val id: String?,
    @SerialName("images")
    val images: List<Image>?,
    @SerialName("name")
    val name: String?,
    @SerialName("owner")
    val owner: Owner?,
    @SerialName("primary_color")
    val primaryColor: String?,
    @SerialName("public")
    val `public`: Boolean?,
    @SerialName("snapshot_id")
    val snapshotId: String?,
    @SerialName("tracks")
    val tracks: Tracks?,
    @SerialName("type")
    val type: String?,
    @SerialName("uri")
    val uri: String?
)