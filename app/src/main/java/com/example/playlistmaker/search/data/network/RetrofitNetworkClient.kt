package com.example.playlistmaker.search.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class RetrofitNetworkClient (): NetworkClient {
    private val itunesBaseUrl = "https://itunes.apple.com"
    val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val iTunesService = retrofit.create(ITunesSearchAPI::class.java)

    override fun doRequest(dto: Any): Response {
        try {
            if (dto is TrackSearchRequest) {
                val resp = iTunesService
                    .findTrack(dto.expression)
                    .execute()

                val body = resp.body() ?: Response()

                return body.apply { resultCode = resp.code() }
            } else {
                return Response().apply { resultCode = 400 }
            }
        }
        catch (ex: Exception){
            return Response().apply { resultCode = 484 }
        }
    }
}