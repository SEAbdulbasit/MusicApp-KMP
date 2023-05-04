package com.example.musicapp_kmp.network.models.topfiftycharts


import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class VideoThumbnail(
    @SerialName("url")
    val url: String?
):Parcelable