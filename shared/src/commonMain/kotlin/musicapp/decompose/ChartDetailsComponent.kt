package musicapp.decompose

import musicapp.chartdetails.ChartDetailsViewModel
import musicapp.network.models.topfiftycharts.Item
import kotlinx.serialization.Serializable
import musicapp.playerview.CountdownViewModel


/**
 * Created by abdulbasit on 19/03/2023.
 */

interface ChartDetailsComponent {
    val viewModel: ChartDetailsViewModel
    val countdownViewModel: CountdownViewModel
    fun onOutPut(output: Output)
    sealed class Output {
        data object GoBack : Output()
        data class OnPlayAllSelected(val playlist: List<Item>) : Output()
        data class OnTrackSelected(val trackId: String, val playlist: List<Item>) : Output()
    }

    fun onSleepTimerExpired()

    @Serializable
    sealed interface Input {

        @Serializable
        data class TrackUpdated(val trackId: String) : Input
    }
}
