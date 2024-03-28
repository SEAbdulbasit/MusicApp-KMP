package musicapp_kmp.network

import musicapp_kmp.network.models.featuredplaylist.FeaturedPlayList
import musicapp_kmp.network.models.newreleases.NewReleasedAlbums
import musicapp_kmp.network.models.topfiftycharts.TopFiftyCharts


/**
 * Created by abdulbasit on 26/02/2023.
 */
interface SpotifyApi {
    suspend fun getTopFiftyChart(): TopFiftyCharts
    suspend fun getNewReleases(): NewReleasedAlbums
    suspend fun getFeaturedPlaylist(): FeaturedPlayList
    suspend fun getPlayList(playlistId: String): TopFiftyCharts
}