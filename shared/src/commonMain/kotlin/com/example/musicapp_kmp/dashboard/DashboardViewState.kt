package com.example.musicapp_kmp.dashboard

import com.example.musicapp_kmp.network.models.newreleases.NewReleasedAlbums
import com.example.musicapp_kmp.network.models.topfiftycharts.TopFiftyCharts


/**
 * Created by abdulbasit on 26/02/2023.
 */
sealed interface DashboardViewState {
    object Loading : DashboardViewState
    data class Success(val topFiftyCharts: TopFiftyCharts, val newReleasedAlbums: NewReleasedAlbums) :
        DashboardViewState

    data class Failure(val error: String) : DashboardViewState
}
