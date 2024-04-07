package com.example.playlistmaker.library.domain.db

import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.player.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun addPlaylist(playlist: Playlist)
    suspend fun removePlaylist(playlistId: Long)
    suspend fun getPlaylists():Flow<List<Playlist>>
    suspend fun getPlaylistTrackList(playlistId: Long): Flow<List<Track>>
    suspend fun insertTrack(playlistId: Long, track: Track): Boolean
    suspend fun deleteTrack(playlistId: Long, track: Track)
    suspend fun getPlaylist(playlistId: Long): Playlist
    suspend fun updatePlaylist(playlistId: Long, name: String, description: String, uriString: String)
}