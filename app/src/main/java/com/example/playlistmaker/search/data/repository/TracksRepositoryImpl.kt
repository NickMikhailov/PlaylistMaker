package com.example.playlistmaker.search.data.repository

import com.example.playlistmaker.search.domain.TracksRepository
import com.example.playlistmaker.player.domain.models.Track
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.data.network.TrackSearchRequest
import com.example.playlistmaker.search.data.network.TrackSearchResponse

class TracksRepositoryImpl (private val networkClient: NetworkClient) : TracksRepository {

    override fun getTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(TrackSearchRequest(expression))
        if (response.resultCode == 200) {
            return (response as TrackSearchResponse).results.map {
                it.getTrack()
            }
        } else {
            return emptyList()
        }
    }
}