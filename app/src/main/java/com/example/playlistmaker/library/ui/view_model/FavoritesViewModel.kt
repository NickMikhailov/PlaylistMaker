package com.example.playlistmaker.library.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FavoritesViewModel():ViewModel() {

    private val favoritesStateLiveData = MutableLiveData<FavoritesState>(FavoritesState.Empty)
    fun observeState(): LiveData<FavoritesState> = favoritesStateLiveData

    private fun renderState(state: FavoritesState) {
        this.favoritesStateLiveData.postValue(state)
    }
}