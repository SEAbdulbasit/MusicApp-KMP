package com.example.musicapp_kmp.decompose

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.example.musicapp_kmp.chartdetails.ChartDetailsViewModel
import com.example.musicapp_kmp.network.models.topfiftycharts.Item


/**
 * Created by abdulbasit on 19/03/2023.
 */

interface ChartDetailsComponent {
    val viewModel: ChartDetailsViewModel
    fun onOutPut(output: Output)
    sealed class Output {
        object GoBack : Output()
        data class OnPlayAllSelected(val playlist: List<Item>) : Output()
        data class OnTrackSelected(val trackId: String) : Output()
    }

    @Parcelize
    sealed interface Input : Parcelable {

        @Parcelize
        data class TrackUpdated(val trackId: String) : Input, Parcelable
    }
}
