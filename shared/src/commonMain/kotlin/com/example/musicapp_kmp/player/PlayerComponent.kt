package com.example.musicapp_kmp.player

import com.example.musicapp_kmp.network.models.topfiftycharts.Item


/**
 * Created by abdulbasit on 19/03/2023.
 */

interface PlayerComponent {
    val playerController: MediaPlayerController
    val trackList: List<Item>

    fun onOutPut(output: Output)

    sealed class Output {
        object OnPause : Output()
        object OnPlay : Output()
        data class OnTrackUpdated(val trackId: String) : Output()
        data class RegisterCallbacks(val trackUpdateCallback: (String) -> Unit) : Output()
    }

}
