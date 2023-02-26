package com.example.musicapp_kmp.network.models.topfiftycharts


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImageX(
    @SerialName("height")
    val height: Int?,
    @SerialName("url")
    val url: String?,
    @SerialName("width")
    val width: Int?
)