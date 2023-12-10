package com.example.playlistmaker.search.ui.view_model

import com.example.playlistmaker.player.domain.models.Track
import com.example.playlistmaker.search.domain.models.Placeholder
import com.example.playlistmaker.search.domain.models.SearchHistory

sealed interface TrackSearchState {
    object Loading: TrackSearchState
    data class Content(val trackList:List<Track>): TrackSearchState
    data class Error(val placeholder: Placeholder, val errorMessage:String=""): TrackSearchState
    data class History(val searchHistory:SearchHistory): TrackSearchState

}
