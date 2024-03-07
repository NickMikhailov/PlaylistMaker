package com.example.playlistmaker.library.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.db.FavoritesInteractor
import com.example.playlistmaker.player.domain.models.Track
import com.example.playlistmaker.search.ui.view_model.SingleEventLiveData
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoritesInteractor: FavoritesInteractor
):ViewModel() {

    private val showPlayerTrigger = SingleEventLiveData<Track>()
    private val favoritesStateLiveData = MutableLiveData<FavoritesState>(FavoritesState.Empty)
    fun observeState(): LiveData<FavoritesState> = favoritesStateLiveData
    fun observeStartPlayerEvent(): LiveData<Track> = showPlayerTrigger

    private fun renderState(state: FavoritesState) {
        this.favoritesStateLiveData.postValue(state)
    }
    fun showPlayer(track: Track) {
//        searchHistoryInteractor.addToHistory(track)
        showPlayerTrigger.value = track
    }
    fun updateFavorites() {
        viewModelScope.launch {
            favoritesInteractor
                .getFavoritesTrackList()
                .collect { favoritesTracks ->
                    if (favoritesTracks.isNotEmpty()) {
                        renderState(FavoritesState.Content(favoritesTracks))
                    } else {
                        renderState(FavoritesState.Empty)
                    }
                }
        }
    }
}