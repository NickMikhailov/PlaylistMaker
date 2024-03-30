package com.example.playlistmaker.library.domain.impl

import com.example.playlistmaker.library.domain.db.PlaylistInteractor
import com.example.playlistmaker.library.domain.db.PlaylistRepository
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.player.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val playlistRepository: PlaylistRepository

    ): PlaylistInteractor {
    override suspend fun addPlaylist(playlist: Playlist) {
        playlistRepository.addPlaylist(playlist)
    }

    override suspend fun removePlaylist(playlistId: Long) {
        playlistRepository.removePlaylist(playlistId)
    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.getPlaylists()
    }

    override suspend fun getPlaylistTrackList(playlistId: Long): Flow<List<Track>> {
        return playlistRepository.getPlaylistTrackList(playlistId)
    }

    override suspend fun insertTrack(playlistId: Long, track: Track): Boolean {
        return playlistRepository.insertTrack(playlistId,track)
    }

    override suspend fun deleteTrack(playlistId: Long, track: Track) {
        playlistRepository.deleteTrack(playlistId,track)
    }

    override suspend fun getPlaylist(playlistId: Long): Playlist {
        return playlistRepository.getPlaylist(playlistId)
    }

    override suspend fun updatePlaylist(playlistId:Long, name: String, description: String, uriString: String) {
        playlistRepository.updatePlaylist(playlistId, name, description, uriString)
    }

}