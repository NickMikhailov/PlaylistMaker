package com.example.playlistmaker.library.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.db.PlaylistInteractor
import com.example.playlistmaker.library.domain.models.Playlist
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistInteractor: PlaylistInteractor

):ViewModel() {

    private val playlistsStateLiveData = MutableLiveData<PlaylistsState>(PlaylistsState.Empty)
    fun observeState(): LiveData<PlaylistsState> = playlistsStateLiveData

    private fun renderState(state: PlaylistsState) {
        this.playlistsStateLiveData.postValue(state)
    }

    fun getPlaylists() {
        viewModelScope.launch {
            playlistInteractor
                .getPlaylists()
                .collect(){
                        playlists ->
                    if (playlists.isNotEmpty()) {
                        renderState(PlaylistsState.Content(playlists))
                    } else {
                        renderState(PlaylistsState.Empty)
                    }
                }
        }

    }

    fun showPlaylist(position: Int) {

    }
}