package com.example.playlistmaker.library.data.db

import com.example.playlistmaker.library.data.db.entity.PlaylistEntity
import com.example.playlistmaker.library.domain.models.Playlist

class PlaylistDbConverter {
    fun map(playlistEntity: PlaylistEntity): Playlist {
        return Playlist(
            playlistEntity.playlistId,
            playlistEntity.name,
            playlistEntity.description,
            playlistEntity.trackListGson,
            playlistEntity.trackCount,
            playlistEntity.coverName
        )
    }

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlist.id,
            playlist.name,
            playlist.description,
            playlist.trackListGson,
            playlist.trackCount,
            playlist.coverName
        )
    }
}