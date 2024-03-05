package com.example.playlistmaker.search.domain

import com.example.playlistmaker.player.domain.models.Track

interface SearchHistoryInteractor {
    fun addToHistory(track: Track)
    fun clearHistory()
    fun getHistory(): List<Track>
}