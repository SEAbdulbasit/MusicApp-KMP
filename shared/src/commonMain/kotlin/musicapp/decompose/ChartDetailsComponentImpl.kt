package musicapp.decompose

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import musicapp.chartdetails.ChartDetailsViewModel
import musicapp.network.SpotifyApi
import kotlinx.coroutines.flow.SharedFlow
import musicapp.playerview.CountdownViewModel


/**
 * Created by abdulbasit on 19/03/2023.
 */
class ChartDetailsComponentImpl(
    componentContext: ComponentContext,
    val spotifyApi: SpotifyApi,
    val playlistId: String,
    val playingTrackId: String,
    val chatDetailsInput: SharedFlow<ChartDetailsComponent.Input>,
    val sleepTimerExpired:()-> Unit,
    val output: (ChartDetailsComponent.Output) -> Unit,
) : ChartDetailsComponent, ComponentContext by componentContext {
    override val viewModel: ChartDetailsViewModel
        get() = instanceKeeper.getOrCreate {
            ChartDetailsViewModel(
                spotifyApi,
                playlistId,
                playingTrackId,
                chatDetailsInput
            )
        }

    override val countdownViewModel: CountdownViewModel
        get() = CountdownViewModel()

    override fun onOutPut(output: ChartDetailsComponent.Output) {
        output(output)
    }

    override fun onSleepTimerExpired(){
       sleepTimerExpired()
    }
}