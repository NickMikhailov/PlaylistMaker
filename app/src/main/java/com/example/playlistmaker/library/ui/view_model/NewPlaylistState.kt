package com.example.playlistmaker.library.ui.view_model

sealed interface NewPlaylistState {
    object Disabled : NewPlaylistState
    object Enabled : NewPlaylistState

}