package com.example.playlistmaker.library.ui.view_model

import android.os.Bundle
import android.provider.MediaStore.Audio.Playlists.Members.PLAYLIST_ID
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.library.domain.db.PlaylistInteractor
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.library.ui.activity.FavoritesFragment
import com.example.playlistmaker.player.domain.models.Track
import com.example.playlistmaker.search.ui.view_model.SingleEventLiveData
import com.google.gson.Gson
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistInteractor: PlaylistInteractor

):ViewModel() {

    private val showPlaylistTrigger = SingleEventLiveData<Long?>()
    private val playlistsStateLiveData = MutableLiveData<PlaylistsState>(PlaylistsState.Empty)
    fun observeState(): LiveData<PlaylistsState> = playlistsStateLiveData
    fun observeShowPlaylistEvent(): LiveData<Long?> = showPlaylistTrigger

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
        viewModelScope.launch {
            playlistInteractor
                .getPlaylists()
                .collect() { playlists ->
                    showPlaylistTrigger.value = playlists[position].id
                }
        }
    }
}