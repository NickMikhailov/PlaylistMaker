package com.example.playlistmaker.library.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlaylistsViewModel():ViewModel() {

    private val playlistsStateLiveData = MutableLiveData<PlaylistsStates>(PlaylistsStates.Empty)
    fun observeState(): LiveData<PlaylistsStates> = playlistsStateLiveData

    private fun renderState(state: PlaylistsStates) {
        this.playlistsStateLiveData.postValue(state)
    }
}