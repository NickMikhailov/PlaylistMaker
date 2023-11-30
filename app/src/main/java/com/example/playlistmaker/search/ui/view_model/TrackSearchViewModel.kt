package com.example.playlistmaker.search.ui.view_model

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.search.domain.models.DateTimeUtil
import com.example.playlistmaker.player.domain.models.Track
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.search.domain.TracksConsumer
import com.example.playlistmaker.search.domain.models.Placeholder
import com.example.playlistmaker.search.domain.models.SearchHistory


class TrackSearchViewModel(private final val application: Application) : AndroidViewModel(application) {
    private val tracksInteractor = Creator.provideTracksInteractor()
    private val handler = Handler(Looper.getMainLooper())
    private var latestSearchText: String? = null
    private var trackList = mutableListOf<Track>()
    private var sharedPreferences = application.getSharedPreferences(TRACK_HISTORY, Context.MODE_PRIVATE)
    private var searchHistory = SearchHistory(sharedPreferences)

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
                        renderState(TrackSearchState.Error(Placeholder.ERROR, ERROR_MESSAGE))
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
        searchHistory.addToHistory(track)
        showPlayerTrigger.value = track
    }
    fun showHistory(){
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        sharedPreferences = application.getSharedPreferences(TRACK_HISTORY, Context.MODE_PRIVATE)
        searchHistory.update(sharedPreferences)
        if(searchHistory.getHistory().size!=0) {
            renderState(TrackSearchState.History(searchHistory))
        } else {
            renderState(TrackSearchState.Error(Placeholder.EMPTY))
        }
    }
    fun clearHistory() {
        searchHistory.clearHistory()
        renderState(TrackSearchState.Error(Placeholder.EMPTY))
    }
    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }
    companion object {
        private val SEARCH_REQUEST_TOKEN = Any()
        private const val TRACK_HISTORY = "track_history"
        private const val ERROR_MESSAGE = "Проблемы с сетью. Попробуйте еще раз."
        fun factory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                TrackSearchViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }
}