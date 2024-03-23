package com.example.playlistmaker.library.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.db.PlaylistInteractor
import com.example.playlistmaker.player.domain.models.Track
import com.example.playlistmaker.search.ui.view_model.SingleEventLiveData
import com.example.playlistmaker.sharing.SharingInteractor
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.Exception

class PlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val sharingInteractor: SharingInteractor

) : ViewModel() {
    private val showPlayerTrigger = SingleEventLiveData<Track>()
    private val playlistStateLiveData = MutableLiveData<PlaylistState>(PlaylistState.Empty)
    fun observeState(): LiveData<PlaylistState> = playlistStateLiveData
    fun observeStartPlayerEvent(): LiveData<Track> = showPlayerTrigger

    private fun renderState(state: PlaylistState) {
        this.playlistStateLiveData.postValue(state)
    }

    fun showPlayer(track: Track) {
        showPlayerTrigger.value = track
    }

    fun getPlaylist(playlistId: String?) {
        try {
            val id: Long? = playlistId?.toLongOrNull()
            if (id != null) {
                viewModelScope.launch {
                    val playlist = playlistInteractor.getPlaylist(id)
                    var trackList = mutableListOf<Track>()
                    if (playlist.trackListGson != "") {
                        val tracks: List<Track> = Gson().fromJson(
                            playlist.trackListGson,
                            object : TypeToken<List<Track>>() {}.type
                        )
                        trackList.addAll(tracks)
                    }
                    renderState(PlaylistState.Content(playlist, trackList))
                }
            } else {
                renderState(PlaylistState.Empty)
            }
        } catch (e: Exception) {
            renderState(PlaylistState.Empty)
        }
    }

    fun removeTrackFromPlaylist(playlistId: String?, selectedTrack: Track) {
        try {
            val id: Long? = playlistId?.toLongOrNull()
            if (id != null) {
                viewModelScope.launch {
                    playlistInteractor.deleteTrack(id, selectedTrack)
                    getPlaylist(playlistId)
                }
            }
        } catch (e: Exception) {
            renderState(PlaylistState.Empty)
        }
    }

    fun sharePlaylist(playlistId: String?) {
        sharingInteractor.sharePlaylist()
    }
}

