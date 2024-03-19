package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.player.domain.models.Track
import com.example.playlistmaker.search.domain.TracksInteractor
import com.example.playlistmaker.search.domain.TracksRepository
import kotlinx.coroutines.flow.Flow

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {

    override fun search(expression: String): Flow<List<Track>?> {
        return repository.getTracks(expression)
    }
}