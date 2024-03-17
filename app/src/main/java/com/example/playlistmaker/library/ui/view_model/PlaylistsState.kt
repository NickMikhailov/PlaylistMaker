package com.example.playlistmaker.library.ui.view_model

import com.example.playlistmaker.library.domain.models.Playlist

sealed interface PlaylistsState {
    object Empty: PlaylistsState
    data class Content(val playlists:List<Playlist>): PlaylistsState
}