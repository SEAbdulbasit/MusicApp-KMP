package com.example.musicapp_kmp.network.models.featuredplaylist


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Item(
    @SerialName("collaborative")
    val collaborative: Boolean?,
    @SerialName("description")
    val description: String?,
    @SerialName("external_urls")
    val externalUrls: ExternalUrls?,
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
    val `public`: String?,
    @SerialName("snapshot_id")
    val snapshotId: String?,
    @SerialName("tracks")
    val tracks: Tracks?,
    @SerialName("type")
    val type: String?,
    @SerialName("uri")
    val uri: String?
)