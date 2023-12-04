package com.example.playlistmaker.search.domain
import com.example.playlistmaker.player.domain.models.Track

interface TracksConsumer {
    fun consume(foundTracks:List<Track>?)
}