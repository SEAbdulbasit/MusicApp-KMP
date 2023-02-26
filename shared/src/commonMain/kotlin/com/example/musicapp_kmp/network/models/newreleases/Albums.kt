package com.example.musicapp_kmp.network.models.newreleases


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Albums(
    @SerialName("href")
    val href: String?,
    @SerialName("items")
    val items: List<Item>?,
    @SerialName("limit")
    val limit: Int?,
    @SerialName("next")
    val next: String?,
    @SerialName("offset")
    val offset: Int?,
    @SerialName("previous")
    val previous: String?,
    @SerialName("total")
    val total: Int?
)