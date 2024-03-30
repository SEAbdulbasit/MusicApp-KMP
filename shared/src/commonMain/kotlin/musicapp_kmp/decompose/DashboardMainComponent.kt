package musicapp_kmp.decompose

import musicapp_kmp.dashboard.DashboardViewModel


/**
 * Created by abdulbasit on 19/03/2023.
 */
interface DashboardMainComponent {
    val viewModel: DashboardViewModel

    fun onOutPut(output: Output)

    sealed class Output {
        data class PlaylistSelected(val playlistId: String) : Output()
    }
}
