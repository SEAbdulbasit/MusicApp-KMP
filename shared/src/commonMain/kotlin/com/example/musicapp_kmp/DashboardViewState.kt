package com.example.musicapp_kmp

import com.example.musicapp_kmp.network.models.newreleases.NewReleasedAlbums
import com.example.musicapp_kmp.network.models.topfiftycharts.TopFiftyCharts


/**
 * Created by abdulbasit on 26/02/2023.
 */
sealed interface DashboardViewState {
    object Loading : DashboardViewState
    data class Success(val topFiftyCharts: TopFiftyCharts, val newReleasedAlbums: NewReleasedAlbums)
    data class Failure(val error: String)
}
