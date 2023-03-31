package com.example.musicapp_kmp.dashboard

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.example.musicapp_kmp.network.SpotifyApi
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow


/**
 * Created by abdulbasit on 26/02/2023.
 */
class DashboardViewModel(api: SpotifyApi) : InstanceKeeper.Instance {
    val dashboardState = MutableStateFlow<DashboardViewState>(DashboardViewState.Loading)

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        exception.printStackTrace()
        dashboardState.value = DashboardViewState.Failure(exception.message.toString())
    }

    private val job = SupervisorJob()
    private val viewModelScope = CoroutineScope(Dispatchers.Main + coroutineExceptionHandler + job)

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
                dashboardState.value = DashboardViewState.Failure(e.message.toString())
            }
        }
    }

    override fun onDestroy() {
        viewModelScope.cancel()
    }
}