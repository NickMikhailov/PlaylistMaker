package com.example.playlistmaker.library.ui.view_model

import com.example.playlistmaker.library.domain.models.Playlist

sealed interface EditPlaylistState {
    object Empty: EditPlaylistState
    data class Content(val playlist:Playlist): EditPlaylistState
}