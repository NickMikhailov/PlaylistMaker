package com.example.playlistmaker.search.data.repository

import com.example.playlistmaker.library.data.db.AppDatabase
import com.example.playlistmaker.search.domain.TracksRepository
import com.example.playlistmaker.player.domain.models.Track
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.data.network.TrackSearchRequest
import com.example.playlistmaker.search.data.network.TrackSearchResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.net.HttpURLConnection.HTTP_NOT_FOUND
import java.net.HttpURLConnection.HTTP_OK

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val appDatabase: AppDatabase
    ) : TracksRepository {

    override fun getTracks(expression: String): Flow<List<Track>?> = flow {
        val response = networkClient.doTrackSearchRequest(TrackSearchRequest(expression))
        when (response.resultCode) {
            HTTP_OK -> {
                with(response as TrackSearchResponse) {
                    val data = results.map {
                        val isFavorite = isFavorite(it.trackId)
                        it.getTrack(isFavorite)
                    }
                    emit(data)
                }
            }

            HTTP_NOT_FOUND -> {
                emit(null)
            }
        }
    }
    private suspend fun isFavorite(trackId: Int): Boolean{
        val favoritesList = appDatabase.trackDao().getTrackIdList()
        return favoritesList.contains(trackId)
    }

}
