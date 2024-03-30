package com.example.musicapp_kmp.decompose

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.example.musicapp_kmp.network.SpotifyApi
import com.example.musicapp_kmp.network.models.topfiftycharts.Item
import com.example.musicapp_kmp.player.MediaPlayerController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/**
 * Created by abdulbasit on 19/03/2023.
 */
class MusicRootImpl(
    componentContext: ComponentContext,
    private val mediaPlayerController: MediaPlayerController,
    private val dashboardMain: (ComponentContext, (DashboardMainComponent.Output) -> Unit) -> DashboardMainComponent,
    private val chartDetails: (
        ComponentContext, playlistId: String, playingTrackId: String, chatDetailsInput: SharedFlow<ChartDetailsComponent.Input>, (ChartDetailsComponent.Output) -> Unit
    ) -> ChartDetailsComponent,
) : MusicRoot, ComponentContext by componentContext {

    //to keep track of the playing track
    private var currentPlayingTrack = "-1"
    private val musicPlayerInput = MutableSharedFlow<PlayerComponent.Input>()
    private val chatDetailsInput = MutableSharedFlow<ChartDetailsComponent.Input>()

    constructor(
        componentContext: ComponentContext,
        api: SpotifyApi,
        mediaPlayerController: MediaPlayerController
    ) : this(componentContext = componentContext,
        mediaPlayerController = mediaPlayerController,
        dashboardMain = { childContext, output ->
            DashboardMainComponentImpl(
                componentContext = childContext, spotifyApi = api, output = output
            )
        },
        chartDetails = { childContext, playlistId, playingTrackId, chartDetailsInput, output ->
            ChartDetailsComponentImpl(
                componentContext = childContext,
                spotifyApi = api,
                playlistId = playlistId,
                output = output,
                playingTrackId = playingTrackId,
                chatDetailsInput = chartDetailsInput
            )
        })

    private val navigation = StackNavigation<Configuration>()
    private val dialogNavigation = SlotNavigation<DialogConfig>()

    private val stack = childStack(
        source = navigation,
        serializer = serializer(),
        initialConfiguration = Configuration.Dashboard,
        handleBackButton = true,
        childFactory = ::createChild
    )

    private fun createChild(
        configuration: Configuration, componentContext: ComponentContext
    ): MusicRoot.Child = when (configuration) {
        Configuration.Dashboard -> MusicRoot.Child.Dashboard(
            dashboardMain(componentContext, ::dashboardOutput)
        )

        is Configuration.Details -> MusicRoot.Child.Details(
            chartDetails(
                componentContext,
                configuration.playlistId,
                currentPlayingTrack,
                chatDetailsInput,
                ::detailsOutput
            )
        )
    }

    private fun dashboardOutput(output: DashboardMainComponent.Output) {
        when (output) {
            is DashboardMainComponent.Output.PlaylistSelected -> navigation.push(
                Configuration.Details(
                    output.playlistId, currentPlayingTrack
                )
            )
        }
    }

    private fun detailsOutput(output: ChartDetailsComponent.Output) {
        when (output) {
            is ChartDetailsComponent.Output.GoBack -> navigation.pop()
            is ChartDetailsComponent.Output.OnPlayAllSelected -> {
                dialogNavigation.activate(DialogConfig(output.playlist))
                CoroutineScope(Dispatchers.Default).launch {
                    musicPlayerInput.emit(PlayerComponent.Input.UpdateTracks(output.playlist))
                }
            }

            is ChartDetailsComponent.Output.OnTrackSelected -> {
                CoroutineScope(Dispatchers.Default).launch {
                    musicPlayerInput.emit(PlayerComponent.Input.PlayTrack(output.trackId))
                }
            }
        }
    }

    private val player = childSlot<DialogConfig, PlayerComponent>(source = dialogNavigation,
        persistent = false,
        handleBackButton = true,
        childFactory = { config, _ ->
            PlayerComponentImpl(componentContext = componentContext,
                mediaPlayerController = mediaPlayerController,
                trackList = config.playlist,
                playerInputs = musicPlayerInput,
                output = {
                    when (it) {
                        PlayerComponent.Output.OnPause -> TODO()
                        PlayerComponent.Output.OnPlay -> TODO()

                        is PlayerComponent.Output.OnTrackUpdated -> {
                            CoroutineScope(Dispatchers.Default).launch {
                                currentPlayingTrack = it.trackId
                                chatDetailsInput.emit(ChartDetailsComponent.Input.TrackUpdated(it.trackId))
                            }
                        }
                    }
                })
        })

    override val childStack: Value<ChildStack<*, MusicRoot.Child>>
        get() = value()

    override val dialogOverlay: Value<ChildSlot<*, PlayerComponent>>
        get() = player

    private fun value() = stack

    @Serializable
    private sealed class Configuration {
        @Serializable
        data object Dashboard : Configuration()

        @Serializable
        data class Details(
            val playlistId: String,
            val playingTrackId: String,
        ) : Configuration()
    }

    @Parcelize
    private data class DialogConfig(
        val playlist: List<Item>
    ) : Parcelable
}
