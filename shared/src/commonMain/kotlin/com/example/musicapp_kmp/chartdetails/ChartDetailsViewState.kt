package com.example.musicapp_kmp.chartdetails

import com.example.musicapp_kmp.network.models.featuredplaylist.FeaturedPlayList
import com.example.musicapp_kmp.network.models.newreleases.NewReleasedAlbums
import com.example.musicapp_kmp.network.models.topfiftycharts.TopFiftyCharts


/**
 * Created by abdulbasit on 26/02/2023.
 */
sealed interface ChartDetailsViewState {
    object Loading : ChartDetailsViewState
    data class Success(
        val chartDetails: TopFiftyCharts,
    ) : ChartDetailsViewState

    data class Failure(val error: String) : ChartDetailsViewState
}
