package com.example.musicapp_kmp.decompose

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.*
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.example.musicapp_kmp.network.SpotifyApi

/**
 * Created by abdulbasit on 19/03/2023.
 */
class MusicRootImpl(
    componentContext: ComponentContext,
    private val dashboardMain: (ComponentContext, (DashboardMainComponent.Output) -> Unit) -> DashboardMainComponent,
    private val chartDetails: (ComponentContext, playlistId: String, (ChartDetailsComponent.Output) -> Unit) -> ChartDetailsComponent,
) : MusicRoot, ComponentContext by componentContext {
    constructor(componentContext: ComponentContext, api: SpotifyApi) :
            this(componentContext = componentContext,
                dashboardMain = { childContext, output ->
                    DashboardMainComponentImpl(
                        componentContext = childContext,
                        spotifyApi = api,
                        output = output
                    )
                },
                chartDetails = { childContext, playlistId, output ->
                    ChartDetailsComponentImpl(
                        componentContext = childContext,
                        spotifyApi = api,
                        playlistId = playlistId,
                        output = output
                    )
                })


    private val navigation = StackNavigation<Configuration>()

    private val stack = childStack(
        source = navigation,
        initialConfiguration = Configuration.Dashboard,
        handleBackButton = true,
        childFactory = ::createChild
    )

    private fun createChild(configuration: Configuration, componentContext: ComponentContext): MusicRoot.Child =
        when (configuration) {
            Configuration.Dashboard -> MusicRoot.Child.Dashboard(dashboardMain(componentContext, ::dashboardOutput))
            is Configuration.Details -> MusicRoot.Child.Details(
                chartDetails(
                    componentContext,
                    configuration.playlistId,
                    ::detailsOutput
                )
            )
        }

    private fun dashboardOutput(output: DashboardMainComponent.Output) {
        when (output) {
            is DashboardMainComponent.Output.PlaylistSelected -> navigation.push(Configuration.Details(output.playlistId))
        }
    }

    private fun detailsOutput(output: ChartDetailsComponent.Output) {
        when (output) {
            is ChartDetailsComponent.Output.GoBack -> navigation.pop()
            is ChartDetailsComponent.Output.OnPlayAllSelected -> {}
        }
    }

    override val childStack: Value<ChildStack<*, MusicRoot.Child>>
        get() = value()

    private fun value() = stack

    private sealed class Configuration : Parcelable {
        @Parcelize
        object Dashboard : Configuration()

        @Parcelize
        data class Details(val playlistId: String) : Configuration()
    }
}

