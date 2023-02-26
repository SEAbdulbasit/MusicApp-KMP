package com.example.musicapp_kmp.network.models.newreleases


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Artist(
    @SerialName("external_urls")
    val externalUrls: ExternalUrlsX?,
    @SerialName("href")
    val href: String?,
    @SerialName("id")
    val id: String?,
    @SerialName("name")
    val name: String?,
    @SerialName("type")
    val type: String?,
    @SerialName("uri")
    val uri: String?
)