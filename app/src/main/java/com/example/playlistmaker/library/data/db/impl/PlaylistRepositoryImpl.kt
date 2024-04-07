package com.example.playlistmaker.library.data.db.impl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.appcompat.content.res.AppCompatResources
import com.example.playlistmaker.R
import com.example.playlistmaker.library.data.db.AppDatabase
import com.example.playlistmaker.library.data.db.PlaylistDbConverter
import com.example.playlistmaker.library.domain.db.PlaylistRepository
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.player.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConverter: PlaylistDbConverter,
    private val applicationContext: Context
) : PlaylistRepository {
    override suspend fun addPlaylist(playlist: Playlist) {
        playlist.id = appDatabase.playlistDao().addPlaylist(playlistDbConverter.map(playlist))
        playlist.coverName = saveImageToPrivateStorage(playlist.coverName)
        appDatabase.playlistDao().saveCoverPath(playlist.id, playlist.coverName)
    }
    override suspend fun updatePlaylist(playlistId: Long, name: String, description: String, uriString: String) {
        appDatabase.playlistDao().updatePlaylist(playlistId, name, description)
        if (uriString!=""){
            val imagePath = saveImageToPrivateStorage(uriString)
            appDatabase.playlistDao().saveCoverPath(playlistId,imagePath)
        }
    }
    override suspend fun removePlaylist(playlistId: Long) {
        val playlist = getPlaylist(playlistId)
        appDatabase.playlistDao().removePlaylist(playlistDbConverter.map(playlist))
    }
    override suspend fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlistsEntities = appDatabase.playlistDao().getPlaylists()
        val playlists = mutableListOf<Playlist>()
        for (playlistEntity in playlistsEntities) {
            playlists.add(playlistDbConverter.map(playlistEntity))
        }
        emit(playlists)
    }
    override suspend fun getPlaylistTrackList(playlistId: Long): Flow<List<Track>> = flow {
        val trackListGson = appDatabase.playlistDao().getTrackListJson(playlistId)
        val listType = object : TypeToken<List<Track>>() {}.type
        val trackList = Gson().fromJson<List<Track>>(trackListGson, listType) ?: emptyList()
        emit(trackList)
    }
    override suspend fun insertTrack(playlistId: Long, track: Track): Boolean {
        var trackAdded = false
        getPlaylistTrackList(playlistId)
            .collect { trackList ->
                if (!trackList.contains(track)) {
                    val newTrackList = mutableListOf<Track>()
                    newTrackList.addAll(trackList)
                    newTrackList.add(track)
                    val trackListGson = Gson().toJson(newTrackList)
                    appDatabase.playlistDao()
                        .updateTrackList(playlistId, trackListGson, newTrackList.size, getDuration(newTrackList))
                    trackAdded = true
                }
            }
        return trackAdded
    }
    override suspend fun deleteTrack(playlistId: Long, track: Track) {
        getPlaylistTrackList(playlistId)
            .collect { trackList ->
                val newTrackList = mutableListOf<Track>()
                newTrackList.addAll(trackList)
                newTrackList.remove(track)
                val trackListGson = Gson().toJson(newTrackList)
                appDatabase.playlistDao()
                    .updateTrackList(playlistId, trackListGson, newTrackList.size, getDuration(newTrackList))
            }
    }
    override suspend fun getPlaylist(playlistId: Long): Playlist {
       val playlistEntity = appDatabase.playlistDao().getPlaylist(playlistId)
        return playlistDbConverter.map(playlistEntity)
    }
    private fun saveImageToPrivateStorage(uriString: String, ):String {
        val filePath =
            File(applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES), ALBUM)
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val currentTimeMillis = System.currentTimeMillis().toString()
        val file = File(filePath, "$currentTimeMillis.jpg")
        Log.d("filesave",file.toString())
        Log.d("filesave",uriString)
        try {
            val uri = Uri.parse(uriString)
            val inputStream = applicationContext.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            BitmapFactory
                .decodeStream(inputStream)
                .compress(Bitmap.CompressFormat.JPEG, QUALITY, outputStream)
        } catch (e: Exception) {
            val drawableImage = AppCompatResources.getDrawable(applicationContext, R.drawable.cover_placeholder)
            if (drawableImage is BitmapDrawable) {
                val bitmap = drawableImage.bitmap
                val outputStream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY, outputStream)
            }
        }
        return file.toString()
    }
    private fun getDuration(trackList: List<Track>):Long {
        var duration = 0L
        for (track in trackList) {
            duration += convertTimeToMillis(track.trackTime)
        }
        return duration
    }
    private fun convertTimeToMillis(time: String): Long {
        val parts = time.split(":")
        val minutes = parts[0].toLong()
        val seconds = parts[1].toLong()
        return (minutes * 60 + seconds) * 1000
    }
    companion object {
        const val QUALITY = 30
        const val ALBUM = "myAlbum"
    }
}