package network

import kotlinx.coroutines.test.runTest
import musicapp.network.SpotifyApi
import musicapp.network.SpotifyApiImpl
import musicapp.network.models.featuredplaylist.FeaturedPlayList
import musicapp.network.models.newreleases.NewReleasedAlbums
import musicapp.network.models.topfiftycharts.TopFiftyCharts
import kotlin.test.Test
import kotlin.test.assertIs

class ApiTest {
    @Test
    fun `call to getTopFiftyChart should return TopFiftyCharts object as response`() =
        runTest {
            val spotifyApiImpl: SpotifyApi =
                SpotifyApiImpl(topFiftyChartsMockEngine)
            val response = spotifyApiImpl.getTopFiftyChart()
            assertIs<TopFiftyCharts>(response)
        }

    @Test
    fun `call to getNewReleases should return NewReleasedAlbums object as response`() =
        runTest {
            val spotifyApiImpl: SpotifyApi = SpotifyApiImpl(newReleasedAlbumsMockEngine)
            val response = spotifyApiImpl.getNewReleases()
            assertIs<NewReleasedAlbums>(response)
        }

    @Test
    fun `call to getFeaturedPlaylist should return FeaturedPlayList object as response`() =
        runTest {
            val spotifyApiImpl: SpotifyApi =
                SpotifyApiImpl(featuredPlaylistsMockEngine)
            val response = spotifyApiImpl.getFeaturedPlaylist()
            assertIs<FeaturedPlayList>(response)
        }
}