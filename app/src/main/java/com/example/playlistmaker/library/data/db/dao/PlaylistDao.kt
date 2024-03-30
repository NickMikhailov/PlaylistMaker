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
    @Query("SELECT * FROM playlists_table WHERE playlistId=:playlistId")
    suspend fun getPlaylist(playlistId: Long): PlaylistEntity
    @Query("SELECT * FROM playlists_table ORDER BY name ASC")
    suspend fun getPlaylists(): List<PlaylistEntity>
    @Query("UPDATE playlists_table SET trackListGson =:trackListGson, trackCount=:trackCount, duration=:duration WHERE playlistId=:playlistId")
    suspend fun updateTrackList(playlistId: Long, trackListGson: String, trackCount: Int, duration: Long)
    @Query("SELECT trackListGson FROM playlists_table WHERE playlistId=:playlistId")
    suspend fun getTrackListJson(playlistId: Long): String
    @Query("UPDATE playlists_table SET coverName =:coverName WHERE playlistId=:playlistId")
    suspend fun saveCoverPath(playlistId: Long, coverName: String)
    @Query("UPDATE playlists_table SET name =:name, description=:description WHERE playlistId=:playlistId")
    suspend fun updatePlaylist(playlistId: Long, name: String, description: String)

}