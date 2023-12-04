package com.example.playlistmaker.search.data.network

interface NetworkClient {
    fun doTrackSearchRequest(dto: TrackSearchRequest): Response
}