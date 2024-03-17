package com.example.playlistmaker.library.ui.view_model

sealed interface NewPlaylistState {
    object Empty : NewPlaylistState
    object Editing : NewPlaylistState

}