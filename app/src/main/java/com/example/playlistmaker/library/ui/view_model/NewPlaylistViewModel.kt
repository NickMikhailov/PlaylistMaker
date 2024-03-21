package com.example.playlistmaker.library.ui.view_model


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.db.PlaylistInteractor
import com.example.playlistmaker.library.domain.models.Playlist
import kotlinx.coroutines.launch


class NewPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    var isEditing = false



    fun createPlayList(name: String, description: String, coverName: String) {
        val playlist = Playlist(0, name, description, "", 0, coverName)
        viewModelScope.launch {
            playlistInteractor.addPlaylist(playlist)
        }
    }
}