package musicapp_kmp.decompose

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.SharedFlow
import musicapp_kmp.chartdetails.ChartDetailsViewModel
import musicapp_kmp.network.SpotifyApi


/**
 * Created by abdulbasit on 19/03/2023.
 */
class ChartDetailsComponentImpl(
    componentContext: ComponentContext,
    val spotifyApi: SpotifyApi,
    val playlistId: String,
    val playingTrackId: String,
    val chatDetailsInput: SharedFlow<ChartDetailsComponent.Input>,
    val output: (ChartDetailsComponent.Output) -> Unit,
) : ChartDetailsComponent, ComponentContext by componentContext {
    override val viewModel: ChartDetailsViewModel
        get() = ChartDetailsViewModel(
            chartDetailsFlow = chatDetailsInput,
            api = spotifyApi,
            playlistId = playlistId,
            playingTrackId = playingTrackId
        )

    override fun onOutPut(output: ChartDetailsComponent.Output) {
        output(output)
    }
}