package com.example.playlistmaker.player.ui.view_model

import android.os.Message
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.player.domain.models.Track


sealed interface BottomSheetState {
    object Default: BottomSheetState
    class PlaylistsEditing(val playlists: List<Playlist>, val track: Track) : BottomSheetState
    class PlaylistEditingComplete(val trackAdded: Boolean, val playlistName: String): BottomSheetState
}