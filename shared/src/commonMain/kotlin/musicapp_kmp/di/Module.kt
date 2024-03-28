package musicapp_kmp.di

import musicapp_kmp.chartdetails.ChartDetailsInputUsecase
import musicapp_kmp.chartdetails.ChartDetailsViewModel
import musicapp_kmp.chartdetails.PlayerInputUsecase
import musicapp_kmp.dashboard.DashboardViewModel
import musicapp_kmp.network.SpotifyApi
import musicapp_kmp.network.SpotifyApiImpl
import musicapp_kmp.player.MediaPlayerController
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


/**
 * Created by abdulbasit on 28/03/2024.
 */
fun commonModule() = module {
    single<SpotifyApi> { SpotifyApiImpl() }
    single<ChartDetailsInputUsecase> { ChartDetailsInputUsecase() }
    single<PlayerInputUsecase> { PlayerInputUsecase() }
    single<MediaPlayerController> { MediaPlayerController(get()) }

    singleOf(::DashboardViewModel)
    singleOf(::ChartDetailsViewModel)
//    singleOf(::PlayerViewModel)
}


expect fun platformModule(): Module
