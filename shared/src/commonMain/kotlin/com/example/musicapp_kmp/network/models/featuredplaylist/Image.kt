package com.example.musicapp_kmp.network.models.featuredplaylist


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Image(
    @SerialName("height")
    val height: String?,
    @SerialName("url")
    val url: String?,
    @SerialName("width")
    val width: String?
)