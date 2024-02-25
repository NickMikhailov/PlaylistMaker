package com.example.playlistmaker.search.domain

import com.example.playlistmaker.player.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TracksInteractor {
    fun search(expression: String): Flow<List<Track>?>
}