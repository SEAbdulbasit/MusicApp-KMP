package com.example.musicapp_kmp.player

interface MediaPlayerListener {
    fun onReady()
    fun onVideoCompleted()
    fun onError()
}
