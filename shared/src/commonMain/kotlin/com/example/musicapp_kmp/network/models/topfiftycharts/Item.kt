package com.example.musicapp_kmp.network.models.topfiftycharts


import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Item(
    @SerialName("added_at")
    val addedAt: String?,
    @SerialName("added_by")
    val addedBy: AddedBy?,
    @SerialName("is_local")
    val isLocal: Boolean?,
    @SerialName("primary_color")
    val primaryColor: String?,
    @SerialName("track")
    val track: Track?,
    @SerialName("video_thumbnail")
    val videoThumbnail: VideoThumbnail?
) : Parcelable