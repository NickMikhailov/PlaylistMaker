package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.library.data.db.AppDatabase
import com.example.playlistmaker.main.data.shared_prefs.AppSharedPreferences
import com.example.playlistmaker.player.domain.models.Track
import com.example.playlistmaker.search.domain.SearchHistoryInteractor
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

class SearchHistoryInteractorImpl(
    private val sharedPreferences: AppSharedPreferences,
    private val appDatabase: AppDatabase
) : SearchHistoryInteractor {
    private var trackListHistory = mutableListOf<Track>()
    override fun addToHistory(track: Track) {
        getHistory()
        if (trackListHistory.contains(track)) {
            trackListHistory.remove(track)
            trackListHistory.add(0, track)
            sharedPreferences.saveString(TRACK_HISTORY, trackListHistory)
        } else {
            if (trackListHistory.size < 10) {
                trackListHistory.add(0, track)
                sharedPreferences.saveString(TRACK_HISTORY, trackListHistory)
            } else {
                trackListHistory.removeAt(9)
                trackListHistory.add(0, track)
                sharedPreferences.saveString(TRACK_HISTORY, trackListHistory)
            }
        }
    }

    override fun clearHistory() {
        trackListHistory.clear()
        sharedPreferences.clear(TRACK_HISTORY)
    }

    override fun getHistory(): List<Track> {
        val jsonString = sharedPreferences.getString(TRACK_HISTORY)
        val json = GsonBuilder().create()
        val itemType = object : TypeToken<MutableList<Track>>() {}.type
        trackListHistory =
            json.fromJson<MutableList<Track>>(jsonString, itemType) ?: mutableListOf()
        return trackListHistory
    }

    override suspend fun updateHistory() {
        for (track in trackListHistory){
            track.isFavorite = isFavorite(track.trackId)
        }
        sharedPreferences.saveString(TRACK_HISTORY, trackListHistory)
    }

    private suspend fun isFavorite(trackId: Int): Boolean {
        val favoritesList = appDatabase.trackDao().getTrackIdList()
        return favoritesList.contains(trackId)
    }

    companion object {
        private const val TRACK_HISTORY = "track_history"
    }
}

