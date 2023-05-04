package com.example.musicapp_kmp.network

import com.example.musicapp_kmp.TOKEN
import com.example.musicapp_kmp.network.models.featuredplaylist.FeaturedPlayList
import com.example.musicapp_kmp.network.models.newreleases.NewReleasedAlbums
import com.example.musicapp_kmp.network.models.topfiftycharts.TopFiftyCharts
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*


/**
 * Created by abdulbasit on 26/02/2023.
 */
class SpotifyApiImpl : SpotifyApi {
    override suspend fun getTopFiftyChart(): TopFiftyCharts {
        return client.get {
            headers {
                sptifyEndPoint("v1/playlists/37i9dQZEVXbMDoHDwVN2tF")
            }
        }.body()
    }

    override suspend fun getNewReleases(): NewReleasedAlbums {
        return client.get {
            headers {
                sptifyEndPoint("v1/browse/new-releases")
            }
        }.body()
    }

    override suspend fun getFeaturedPlaylist(): FeaturedPlayList {
        return client.get {
            headers {
                sptifyEndPoint("v1/browse/featured-playlists")
            }
        }.body()
    }

    override suspend fun getPlayList(playlistId: String): TopFiftyCharts {
        return client.get {
            headers {
                sptifyEndPoint("v1/playlists/$playlistId")
            }
        }.body()
    }

    private val client = HttpClient {
        expectSuccess = true
        install(HttpTimeout) {
            val timeout = 30000L
            connectTimeoutMillis = timeout
            requestTimeoutMillis = timeout
            socketTimeoutMillis = timeout
        }
        install(ContentNegotiation) {
            json(kotlinx.serialization.json.Json { isLenient = true; ignoreUnknownKeys = true })
        }
    }

    private fun HttpRequestBuilder.sptifyEndPoint(path: String) {
        url {
            takeFrom("https://api.spotify.com/v1/")
            encodedPath = path
            headers {
                append(
                    HttpHeaders.Authorization, TOKEN
                )
            }
        }

    }
}