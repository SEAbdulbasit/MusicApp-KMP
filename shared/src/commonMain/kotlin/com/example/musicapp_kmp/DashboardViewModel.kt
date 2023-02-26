package com.example.musicapp_kmp

import com.example.musicapp_kmp.network.SpotifyApiImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * Created by abdulbasit on 26/02/2023.
 */
class DashboardViewModel {
    private val viewModelScope = CoroutineScope(Dispatchers.Main)
    val api = SpotifyApiImpl()

    init {
        viewModelScope.launch {
//            try {
//                val result = api.getTopFiftyChart()
//                println("api result= success${result.tracks?.items?.size} end")
//            } catch (e: Exception) {
//                println("api result= failed ${e.message.toString()}")
//            }

            try {
                val result = api.getNewReleases()
                println("api result= success new releases ${result.albums?.items?.size} end")
            } catch (e: Exception) {
                println("api result= failed new releases ${e.message.toString()}")
            }
        }
    }
}