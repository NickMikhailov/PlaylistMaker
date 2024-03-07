package com.example.playlistmaker.library.domain.impl

import com.example.playlistmaker.library.domain.db.FavoritesInteractor
import com.example.playlistmaker.library.domain.db.FavoritesRepository
import com.example.playlistmaker.player.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(private val favoritesRepository: FavoritesRepository) : FavoritesInteractor {
    override suspend fun addToFavorites(track: Track) {
        favoritesRepository.addToFavorites(track)
    }

    override suspend fun removeFromFavorites(track: Track) {
        favoritesRepository.removeFromFavorites(track)
    }

    override suspend fun getFavoritesTrackList(): Flow<List<Track>> {
        return favoritesRepository.getFavoriteTrackList()
    }

    override suspend fun isFavorite(track: Track): Boolean {
        return favoritesRepository.isFavorite(track)
    }
}