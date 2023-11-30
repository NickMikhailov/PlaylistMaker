package com.example.playlistmaker.search.domain

import com.example.playlistmaker.player.domain.models.Track

interface TracksRepository {
    fun getTracks(expression: String): List<Track>
}