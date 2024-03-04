package com.example.playlistmaker.library.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.library.data.db.entity.TrackEntity

@Dao
interface TrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Delete(entity = TrackEntity::class)
    suspend fun deleteTrack(trackEntity: TrackEntity)

    @Query("SELECT * FROM tracks_table")
    suspend fun getTrackList(): List<TrackEntity>

    @Query("SELECT trackId FROM tracks_table")
    suspend fun getTrackIdList(): List<Int>

}