package com.example.playlistmaker.search.data.network

interface NetworkClient {
    suspend fun doTrackSearchRequest(dto: TrackSearchRequest): Response
}