package com.example.playlistmaker.search.data.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.net.HttpURLConnection.HTTP_NOT_FOUND
import java.net.HttpURLConnection.HTTP_OK

class RetrofitNetworkClient(private val iTunesService: ITunesSearchAPI) : NetworkClient {

    override suspend fun doTrackSearchRequest(dto: TrackSearchRequest): Response {
        return withContext(Dispatchers.IO) {
            try {
                val resp = iTunesService
                    .findTrack(dto.expression)
                resp.apply { resultCode = HTTP_OK }
            } catch (ex: Exception) {
                Response().apply { resultCode = HTTP_NOT_FOUND }
            }
        }
    }
}