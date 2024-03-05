package com.example.playlistmaker.library.data.db.impl

import com.example.playlistmaker.library.data.db.AppDatabase
import com.example.playlistmaker.library.data.db.TrackDbConverter
import com.example.playlistmaker.library.data.db.entity.TrackEntity
import com.example.playlistmaker.library.domain.db.FavoritesRepository
import com.example.playlistmaker.player.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoritesRepositoryImpl (
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConverter,
) : FavoritesRepository {

    override suspend fun addToFavorites(track: Track) {
        track.isFavorite = true
        appDatabase.trackDao().insertTrack(trackDbConvertor.map(track.copy(isFavorite = true)))
    }

    override suspend fun removeFromFavorites(track: Track) {
        track.isFavorite = false
        appDatabase.trackDao().deleteTrack(trackDbConvertor.map(track.copy(isFavorite = false)))
    }

    override suspend fun getFavoriteTrackList(): Flow<List<Track>> = flow {
        val trackList = appDatabase.trackDao().getTrackList()
        emit(convertFromTrackEntity(trackList))
    }

    override suspend fun isFavorite(track: Track): Boolean {
        return appDatabase
            .trackDao()
            .getTrackIdList()
            .contains(track.trackId)
    }

    private fun convertFromTrackEntity(
        trackList: List<TrackEntity>
    ) = trackList.map (trackDbConvertor::map)
}