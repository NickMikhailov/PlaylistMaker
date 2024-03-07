package com.example.playlistmaker.library.ui.view_model

import com.example.playlistmaker.player.domain.models.Track


sealed interface FavoritesState {
    object Empty: FavoritesState
    data class Content(val favoritesTrackList:List<Track>): FavoritesState

}