package com.example.playlistmaker.presentation.view_model

import com.example.playlistmaker.domain.models.Placeholder
import com.example.playlistmaker.domain.models.Track

sealed interface TrackSearchState {
    object Loading: TrackSearchState
    data class Content(val trackList:List<Track>):TrackSearchState
    data class Error(val placeholder: Placeholder, val errorMessage:String=""):TrackSearchState
    object History:TrackSearchState
}