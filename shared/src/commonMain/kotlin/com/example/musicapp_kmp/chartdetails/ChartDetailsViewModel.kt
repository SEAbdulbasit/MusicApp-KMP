package com.example.musicapp_kmp.chartdetails

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.example.musicapp_kmp.network.SpotifyApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


/**
 * Created by abdulbasit on 26/02/2023.
 */
class ChartDetailsViewModel(api: SpotifyApi, playlistId: String) : InstanceKeeper.Instance {

    private val viewModelScope = CoroutineScope(Dispatchers.Unconfined)
    val chartDetailsViewState = MutableStateFlow<ChartDetailsViewState>(ChartDetailsViewState.Loading)

    init {
        viewModelScope.launch {
            try {
                val playlist = api.getPlayList(playlistId)
                chartDetailsViewState.value = ChartDetailsViewState.Success(
                    chartDetails = playlist
                )
            } catch (e: Exception) {
                e.printStackTrace()
                chartDetailsViewState.value = ChartDetailsViewState.Failure(e.message.toString())
            }
        }
    }

    override fun onDestroy() {
        viewModelScope.cancel()
    }
}