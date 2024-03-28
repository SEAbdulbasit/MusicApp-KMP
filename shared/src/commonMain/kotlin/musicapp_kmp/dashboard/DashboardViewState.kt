package musicapp_kmp.dashboard

import musicapp_kmp.network.models.featuredplaylist.FeaturedPlayList
import musicapp_kmp.network.models.newreleases.NewReleasedAlbums
import musicapp_kmp.network.models.topfiftycharts.TopFiftyCharts


/**
 * Created by abdulbasit on 26/02/2023.
 */
sealed interface DashboardViewState {
    data object Loading : DashboardViewState
    data class Success(
        val topFiftyCharts: TopFiftyCharts,
        val newReleasedAlbums: NewReleasedAlbums,
        val featuredPlayList: FeaturedPlayList
    ) : DashboardViewState

    data class Failure(val error: String) : DashboardViewState
}
