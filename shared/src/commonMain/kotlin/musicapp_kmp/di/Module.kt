package musicapp_kmp.di

import musicapp_kmp.network.SpotifyApi
import musicapp_kmp.network.SpotifyApiImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


/**
 * Created by abdulbasit on 28/03/2024.
 */
fun commonModule() = module {
    single<SpotifyApi> { SpotifyApiImpl() }

//    singleOf(::DashboardViewModel)
//    singleOf(::modul)

}