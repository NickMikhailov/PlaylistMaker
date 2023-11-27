package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface TracksInteractor {
    fun search(expression:String, consumer:TracksConsumer)
//    abstract fun loadTrackData(trackId: String, onComplete: Any)

    interface TracksConsumer{
        fun consume(foundTrack:List<Track>?)
    }
}