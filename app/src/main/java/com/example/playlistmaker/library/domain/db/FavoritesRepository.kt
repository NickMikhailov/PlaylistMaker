package com.example.playlistmaker.library.domain.db

import com.example.playlistmaker.player.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    suspend fun addToFavorites(track: Track)
    suspend fun removeFromFavorites(track:Track)
    suspend fun getFavoriteTrackList(): Flow<List<Track>>
    suspend fun isFavorite(track: Track): Boolean
}