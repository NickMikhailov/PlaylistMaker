package com.example.playlistmaker.search.domain


interface TracksInteractor {
    fun search(expression:String, consumer: TracksConsumer)
}