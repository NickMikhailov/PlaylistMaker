package com.example.playlistmaker.data

import com.example.playlistmaker.data.dto.TrackSearchRequest
import com.example.playlistmaker.data.dto.TrackSearchResponse
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Track

class TracksRepositoryImpl (private val networkClient: NetworkClient) : TracksRepository {

    override fun search(expression: String): List<Track> {
        val response = networkClient.doRequest(TrackSearchRequest(expression))
        if (response.resultCode == 200) {
            return (response as TrackSearchResponse).results.map {
                //здесь нужно преобразовать TrackDto в Track - миллисекунды и обложка
                Track(it.trackId, it.trackName, it.artistName, it.trackTimeMillis, it.artworkUrl100,it.collectionName,it.releaseDate,it.primaryGenreName,it.country,it.previewUrl) }
        } else {
            return emptyList()
        }
    }
}