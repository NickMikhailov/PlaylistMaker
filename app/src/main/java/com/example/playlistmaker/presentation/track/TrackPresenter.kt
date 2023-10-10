package com.example.playlistmaker.presentation.track

import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.models.Track

class TrackPresenter: TracksInteractor.TracksConsumer {
    private val trackList = ArrayList<Track>()
    override fun consume(foundTrack: List<Track>) {
         trackList.addAll(foundTrack)
    }

    fun getTrackList():ArrayList<Track>{
        return trackList
    }
}