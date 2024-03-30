package com.example.playlistmaker.library.ui.view_model

import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.player.domain.models.Track

sealed interface PlaylistState {
    object Empty: PlaylistState
    data class EmptyList(val playlist: Playlist): PlaylistState
    data class Content(val playlist: Playlist, val tracklist: List<Track>): PlaylistState
    object PlaylistDeleted: PlaylistState
}