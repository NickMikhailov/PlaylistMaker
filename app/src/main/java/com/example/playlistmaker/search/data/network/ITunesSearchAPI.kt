package com.example.playlistmaker.search.data.network

import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesSearchAPI {
    @GET("/search?entity=song")
    suspend fun findTrack(@Query("term") text: String): TrackSearchResponse
}