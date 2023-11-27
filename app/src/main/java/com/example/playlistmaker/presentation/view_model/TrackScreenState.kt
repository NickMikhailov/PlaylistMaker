package com.example.playlistmaker.presentation.view_model

import com.example.playlistmaker.domain.models.Track

sealed class TrackScreenState {
    object Loading: TrackScreenState()
    data class Content(
        val trackModel: Track):TrackScreenState()
}