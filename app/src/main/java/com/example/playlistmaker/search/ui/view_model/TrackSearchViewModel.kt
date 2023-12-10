package com.example.playlistmaker.search.ui.view_model

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.DateTimeUtil
import com.example.playlistmaker.player.domain.models.Track
import com.example.playlistmaker.search.domain.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.TracksConsumer
import com.example.playlistmaker.search.domain.TracksInteractor
import com.example.playlistmaker.search.domain.models.Placeholder


class TrackSearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor) : ViewModel() {
    private val handler = Handler(Looper.getMainLooper())
    private var latestSearchText: String? = null
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
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { sendRequest(changedText) }

        val postTime = SystemClock.uptimeMillis() + DateTimeUtil.SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )
    }
    fun sendRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(TrackSearchState.Loading)
            tracksInteractor.search(newSearchText, object : TracksConsumer {
                override fun consume(foundTracks: List<Track>?) {
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
            })
        }
    }
    fun showPlayer(track: Track){
        searchHistoryInteractor.addToHistory(track)
        showPlayerTrigger.value = track
    }
    fun showHistory(){
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        if(searchHistoryInteractor.getHistory().size!=0) {
            renderState(TrackSearchState.History(searchHistoryInteractor))
        } else {
            renderState(TrackSearchState.Error(Placeholder.EMPTY))
        }
    }
    fun clearHistory() {
        searchHistoryInteractor.clearHistory()
        renderState(TrackSearchState.Error(Placeholder.EMPTY))
    }
    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }
    companion object {
        private val SEARCH_REQUEST_TOKEN = Any()
        }
    }
