package com.example.playlistmaker.library.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.library.data.db.entity.PlaylistEntity

@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPlaylist(playlist: PlaylistEntity): Long

    @Delete(entity = PlaylistEntity::class)
    suspend fun removePlaylist(playlistEntity: PlaylistEntity)

    @Query("SELECT * FROM playlists_table ORDER BY name ASC")
    suspend fun getPlaylists(): List<PlaylistEntity>

    @Query("UPDATE playlists_table SET trackListGson =:trackListGson, trackCount=:trackCount WHERE playlistId=:playlistId")
    suspend fun updateTrackList(playlistId: Long, trackListGson: String, trackCount: Int)


    @Query("SELECT trackListGson FROM playlists_table WHERE playlistId=:playlistId")
    suspend fun getPlaylistTrackList(playlistId: Long): String

}