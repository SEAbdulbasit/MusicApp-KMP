package com.example.musicapp_kmp.decompose

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.example.musicapp_kmp.chartdetails.ChartDetailsViewModel
import com.example.musicapp_kmp.network.SpotifyApi
import kotlinx.coroutines.flow.SharedFlow


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
        get() = instanceKeeper.getOrCreate {
            ChartDetailsViewModel(
                spotifyApi,
                playlistId,
                playingTrackId,
                chatDetailsInput
            )
        }

    override fun onOutPut(output: ChartDetailsComponent.Output) {
        output(output)
    }
}