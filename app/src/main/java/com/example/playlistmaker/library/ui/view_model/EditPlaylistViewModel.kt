package com.example.playlistmaker.library.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.db.PlaylistInteractor
import kotlinx.coroutines.launch
import java.lang.Exception

class EditPlaylistViewModel(playlistInteractor: PlaylistInteractor) : NewPlaylistViewModel(
    playlistInteractor
) {
    private val editPlaylistStateLiveData =
        MutableLiveData<EditPlaylistState>(EditPlaylistState.Empty)

    fun observeState(): LiveData<EditPlaylistState> = editPlaylistStateLiveData

    fun getPlaylist(playlistId: String?) {
        viewModelScope.launch {
            try {
                val id = playlistId?.toLong()!!
                val playlist = playlistInteractor.getPlaylist(id)
                renderState(EditPlaylistState.Content(playlist))

            } catch (e: Exception) {
                renderState(EditPlaylistState.Empty)
            }
        }
    }

    private fun renderState(state: EditPlaylistState) {
        editPlaylistStateLiveData.postValue(state)
    }

    fun updatePlayList(playlistId: String?, name: String, description: String, uriString: String) {
        viewModelScope.launch {
            try {
                val id = playlistId?.toLong()!!
                playlistInteractor.updatePlaylist(id, name, description, uriString)
            } catch (e: Exception) {
            }
        }
    }
}