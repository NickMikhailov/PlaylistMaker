package com.example.playlistmaker.library.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.db.PlaylistInteractor
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.main.domain.models.GrammarUtil
import com.example.playlistmaker.player.domain.models.Track
import com.example.playlistmaker.search.ui.view_model.SingleEventLiveData
import com.example.playlistmaker.sharing.SharingInteractor
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import java.lang.Exception
import java.lang.StringBuilder

class PlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val sharingInteractor: SharingInteractor

) : ViewModel() {
    private val playlistStateLiveData = MutableLiveData<PlaylistState>(PlaylistState.Empty)
    private val editPlaylistTrigger = SingleEventLiveData<Playlist>()
    private val showPlayerTrigger = SingleEventLiveData<Track>()
    fun observeState(): LiveData<PlaylistState> = playlistStateLiveData
    fun observeStartPlayerEvent(): LiveData<Track> = showPlayerTrigger
    fun observeEditPlaylistEvent(): LiveData<Playlist> = editPlaylistTrigger
    private fun renderState(state: PlaylistState) {
        this.playlistStateLiveData.postValue(state)
    }

    fun showPlayer(track: Track) {
        showPlayerTrigger.value = track
    }

    fun editPlaylist(playlist: Playlist) {
        editPlaylistTrigger.value = playlist
    }

    fun updatePlaylist(playlistId: String?) {
        val id: Long = playlistId?.toLong()!!
        viewModelScope.launch {
            try {
                val playlist = playlistInteractor.getPlaylist(id)
                val trackList: List<Track> = Gson().fromJson(
                    playlist.trackListGson,
                    object : TypeToken<List<Track>>() {}.type
                )
                renderState(PlaylistState.Content(playlist, trackList))
            } catch (e: Exception) {
                val playlist = playlistInteractor.getPlaylist(id)
                renderState(PlaylistState.EmptyList(playlist))
            }
        }
    }

    fun deletePlaylist(playlistId: String?) {
        try {
            val id: Long = playlistId?.toLong()!!
            viewModelScope.launch {
                playlistInteractor.removePlaylist(id)
                renderState(PlaylistState.PlaylistDeleted)
            }
        } catch (e: Exception) {
        }
    }

    fun deleteTrackFromPlaylist(playlistId: String?, track: Track) {
        try {
            val id: Long = playlistId?.toLong()!!
            viewModelScope.launch {
                playlistInteractor.deleteTrack(id, track)
                updatePlaylist(playlistId)
            }
        } catch (e: Exception) {
        }
    }

    fun sharePlaylist(playlistId: String?) {
        try {
            val id: Long = playlistId?.toLong()!!
            viewModelScope.launch {
                val playlist = playlistInteractor.getPlaylist(id)
                val trackList: List<Track> = Gson().fromJson(
                    playlist.trackListGson,
                    object : TypeToken<List<Track>>() {}.type
                )
                val message = playlistShareMessage(playlist, trackList)
                sharingInteractor.sharePlaylist(message)
            }
        } catch (e: Exception) {
        }
    }

    private fun playlistShareMessage(playlist: Playlist, trackList: List<Track>): String {
        val sb = StringBuilder()
        sb.append("${playlist.name}\n")
        sb.append("${playlist.description}\n")
        sb.append("${playlist.trackCount} ${GrammarUtil.getEnding(playlist.trackCount,"трек","трека","треков")} \n\n")
        trackList.forEachIndexed { index, track ->
            val trackInfo =
                "$index. ${track.artistName} - ${track.trackName} - (${track.trackTime})" // Формат трека: "[номер]. [исполнитель] - [название] ([продолжительность])"
            sb.append("$trackInfo\n")
        }
        return sb.toString()
    }
}

