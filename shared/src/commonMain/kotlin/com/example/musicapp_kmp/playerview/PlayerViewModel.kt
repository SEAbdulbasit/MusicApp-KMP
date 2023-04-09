package com.example.musicapp_kmp.playerview

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.example.musicapp_kmp.network.models.topfiftycharts.Item
import com.example.musicapp_kmp.player.MediaPlayerController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


/**
 * Created by abdulbasit on 26/02/2023.
 */
class PlayerViewModel(mediaPlayerController: MediaPlayerController, trackList: List<Item>) : InstanceKeeper.Instance {
    private val viewModelScope = CoroutineScope(Dispatchers.Unconfined)
    val chartDetailsViewState =
        MutableStateFlow(PlayerViewState(trackList = trackList, mediaPlayerController = mediaPlayerController))

    init {
        viewModelScope.launch {

        }
    }

    override fun onDestroy() {
        viewModelScope.cancel()
    }
}