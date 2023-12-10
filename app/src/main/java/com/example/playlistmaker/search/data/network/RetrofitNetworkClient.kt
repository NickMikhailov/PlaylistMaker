package com.example.playlistmaker.search.data.network

import java.lang.Exception

class RetrofitNetworkClient (private val iTunesService: ITunesSearchAPI): NetworkClient {

    override fun doTrackSearchRequest(dto: TrackSearchRequest): Response {
        return try {
            val resp = iTunesService
                .findTrack(dto.expression)
                .execute()

            val body = resp.body() ?: Response()
            body.apply { resultCode = resp.code() }
        } catch (ex: Exception){
            Response().apply { resultCode = 484 }
        }
    }
}