package com.example.musicapp_kmp.dashboard

import com.example.musicapp_kmp.network.SpotifyApiImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


/**
 * Created by abdulbasit on 26/02/2023.
 */
class DashboardViewModel {
    private val viewModelScope = CoroutineScope(Dispatchers.Main)
    val api = SpotifyApiImpl()
    val dashboardState = MutableStateFlow<DashboardViewState>(DashboardViewState.Loading)

    init {
        viewModelScope.launch {
            try {
                val topFiftyCharts = async { api.getTopFiftyChart() }.await()
                val newReleasedAlbums = async { api.getNewReleases() }.await()
                val featuredPlaylist = async { api.getFeaturedPlaylist() }.await()
                dashboardState.value = DashboardViewState.Success(
                    topFiftyCharts = topFiftyCharts,
                    newReleasedAlbums = newReleasedAlbums,
                    featuredPlayList = featuredPlaylist
                )
            } catch (e: Exception) {
                e.printStackTrace()
                DashboardViewState.Failure(e.message.toString())
            }
        }
    }
}