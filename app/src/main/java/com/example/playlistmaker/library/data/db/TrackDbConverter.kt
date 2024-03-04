package com.example.playlistmaker.library.data.db

import com.example.playlistmaker.library.data.db.entity.TrackEntity
import com.example.playlistmaker.player.domain.models.Track

class TrackDbConverter {


    fun map(trackEntity: TrackEntity): Track {
        return Track(
            trackEntity.trackId,
            trackEntity.trackName,
            trackEntity.artistName,
            trackEntity.trackTime,
            trackEntity.artworkUrl100,
            trackEntity.artworkUrl500,
            trackEntity.collectionName,
            trackEntity.releaseDate,
            trackEntity.primaryGenreName,
            trackEntity.country,
            trackEntity.previewUrl,
            trackEntity.isFavorite
        )
    }

    fun map(track: Track): TrackEntity {
        return TrackEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTime,
            track.artworkUrl100,
            track.artworkUrl500,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl,
            track.isFavorite
        )
    }

}