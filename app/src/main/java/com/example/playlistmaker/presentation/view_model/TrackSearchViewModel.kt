package com.example.playlistmaker.presentation.view_model

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.Creator
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.models.DateTimeUtil
import com.example.playlistmaker.domain.models.Placeholder
import com.example.playlistmaker.domain.models.Track
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory


class TrackSearchViewModel(application: Application) : AndroidViewModel(application) {
    private val tracksInteractor =
        Creator.provideTracksInteractor() //ок getApplication<Application>()
    private val handler = Handler(Looper.getMainLooper()) //ок
    private var latestSearchText: String? = null // ок
    private val stateLiveData = MutableLiveData<TrackSearchState>()
    fun observeState(): LiveData<TrackSearchState> = stateLiveData
    private fun renderState(state: TrackSearchState) {
        stateLiveData.postValue(state)
    }

    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
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
        ) // ок
    }

    companion object {
        private val SEARCH_REQUEST_TOKEN = Any()
        fun factory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                TrackSearchViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }

    fun sendRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(TrackSearchState.Loading)
            tracksInteractor.search(newSearchText, object : TracksInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>?) {
                    val trackList = mutableListOf<Track>()
                    if (foundTracks != null) {
                        trackList.addAll(foundTracks)
                    }
                    when {
//                        errorMessage != null -> {
//                            renderState(TrackSearchState.Error(Placeholder.NOTHING_FOUND, errorMessage))
//                        }

                        trackList.isEmpty() -> {
                            renderState(TrackSearchState.Error(Placeholder.NOTHING_FOUND))
                        }

                        else -> {
                            renderState(TrackSearchState.Content(trackList))
                        }
                    }
                }
            })
        }
    }
}