package musicapp_kmp.dashboard

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import musicapp_kmp.network.SpotifyApi
import org.koin.core.component.KoinComponent


/**
 * Created by abdulbasit on 26/02/2023.
 */
class DashboardViewModel constructor(api: SpotifyApi) : KoinComponent, InstanceKeeper.Instance {
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
}