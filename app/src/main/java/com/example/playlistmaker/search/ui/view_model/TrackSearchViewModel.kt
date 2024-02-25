package com.example.playlistmaker.search.ui.view_model


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.DateTimeUtil
import com.example.playlistmaker.player.domain.models.Track
import com.example.playlistmaker.search.domain.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.TracksInteractor
import com.example.playlistmaker.search.domain.models.Placeholder
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class TrackSearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {
    private var latestSearchText: String? = null
    private var searchJob: Job? = null
    private var trackList = mutableListOf<Track>()

    private val trackSearchStateLiveData = MutableLiveData<TrackSearchState>()
    private val showPlayerTrigger = SingleEventLiveData<Track>()
    fun observeState(): LiveData<TrackSearchState> = trackSearchStateLiveData
    fun observeStartPlayerEvent(): LiveData<Track> = showPlayerTrigger
    private fun renderState(state: TrackSearchState) {
        this.trackSearchStateLiveData.postValue(state)
    }

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }
        this.latestSearchText = changedText
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(DateTimeUtil.SEARCH_DEBOUNCE_DELAY)
            sendRequest(changedText)
        }

    }

    fun sendRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(TrackSearchState.Loading)
            viewModelScope.launch {
                tracksInteractor
                    .search(newSearchText)
                    .collect { foundTracks ->
                        trackList.clear()
                        if (foundTracks != null) {
                            trackList.addAll(foundTracks)
                        } else {
                            renderState(TrackSearchState.Error(Placeholder.ERROR, R.string.error))
                        }

                        if (trackList.isEmpty()) {
                            renderState(TrackSearchState.Error(Placeholder.NOTHING_FOUND))
                        } else {
                            renderState(TrackSearchState.Content(trackList))
                        }
                    }
            }
        }
    }

    fun showPlayer(track: Track) {
        searchHistoryInteractor.addToHistory(track)
        showPlayerTrigger.value = track
    }

    fun showHistory() {
        if (searchHistoryInteractor.getHistory().size != 0) {
            renderState(TrackSearchState.History(searchHistoryInteractor))
        } else {
            renderState(TrackSearchState.Error(Placeholder.EMPTY))
        }
    }

    fun clearHistory() {
        searchHistoryInteractor.clearHistory()
        renderState(TrackSearchState.Error(Placeholder.EMPTY))
    }
}
