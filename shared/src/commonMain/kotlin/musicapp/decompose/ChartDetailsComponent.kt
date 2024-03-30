package musicapp.decompose

import musicapp.chartdetails.ChartDetailsViewModel
import musicapp.network.models.topfiftycharts.Item
import kotlinx.serialization.Serializable


/**
 * Created by abdulbasit on 19/03/2023.
 */

interface ChartDetailsComponent {
    val viewModel: ChartDetailsViewModel
    fun onOutPut(output: Output)
    sealed class Output {
        data object GoBack : Output()
        data class OnPlayAllSelected(val playlist: List<Item>) : Output()
        data class OnTrackSelected(val trackId: String) : Output()
    }

    @Serializable
    sealed interface Input {

        @Serializable
        data class TrackUpdated(val trackId: String) : Input
    }
}
