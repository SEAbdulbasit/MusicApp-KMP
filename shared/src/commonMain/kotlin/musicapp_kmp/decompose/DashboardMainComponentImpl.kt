package musicapp_kmp.decompose

import com.arkivanov.decompose.ComponentContext
import musicapp_kmp.dashboard.DashboardViewModel
import musicapp_kmp.network.SpotifyApi


/**
 * Created by abdulbasit on 19/03/2023.
 */
class DashboardMainComponentImpl(
    componentContext: ComponentContext,
    val output: (DashboardMainComponent.Output) -> Unit,
    val spotifyApi: SpotifyApi
) : DashboardMainComponent, ComponentContext by componentContext {
    override val viewModel: DashboardViewModel
        get() = throw Exception("Somethign went wrong")/*instanceKeeper.getOrCreate { DashboardViewModel(spotifyApi) }*/

    override fun onOutPut(output: DashboardMainComponent.Output) {
        output(output)
    }
}