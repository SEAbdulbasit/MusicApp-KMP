package com.example.musicapp_kmp.player

import com.arkivanov.decompose.ComponentContext
import com.example.musicapp_kmp.network.models.topfiftycharts.Item

class PlayerComponentImpl(
    componentContext: ComponentContext,
    private val mediaPlayerController: MediaPlayerController,
    val output: (PlayerComponent.Output) -> Unit,
    override val trackList: List<Item>,
) : PlayerComponent, ComponentContext by componentContext {

    override val playerController: MediaPlayerController
        get() = mediaPlayerController


    override fun onOutPut(output: PlayerComponent.Output) {
        output(output)
    }

}

