package com.example.musicapp_kmp.decompose

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.example.musicapp_kmp.network.models.topfiftycharts.Item
import com.example.musicapp_kmp.player.MediaPlayerController
import com.example.musicapp_kmp.playerview.PlayerViewModel

class PlayerComponentImpl(
    componentContext: ComponentContext,
    private val mediaPlayerController: MediaPlayerController,
    private val trackList: List<Item>,
    val output: (PlayerComponent.Output) -> Unit,
) : PlayerComponent, ComponentContext by componentContext {

    override val viewModel: PlayerViewModel
        get() = instanceKeeper.getOrCreate {
            PlayerViewModel(
                mediaPlayerController = mediaPlayerController,
                trackList = trackList
            )
        }

    override fun onOutPut(output: PlayerComponent.Output) {
        output(output)
    }

}

