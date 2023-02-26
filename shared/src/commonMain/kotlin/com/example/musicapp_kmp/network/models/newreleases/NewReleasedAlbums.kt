package com.example.musicapp_kmp.network.models.newreleases


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewReleasedAlbums(
    @SerialName("albums")
    val albums: Albums?
)