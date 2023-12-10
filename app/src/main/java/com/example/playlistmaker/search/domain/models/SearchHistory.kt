package com.example.playlistmaker.search.domain.models

import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.player.domain.models.Track
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

class SearchHistory (){
    private var sharedPreferences = Creator.provideSharedPreferences()
    private var trackListHistory = ArrayList<Track>()
    fun addToHistory(track: Track) {
        if (trackListHistory.contains(track)) {
            trackListHistory.remove(track)
            trackListHistory.add(0,track)
            sharedPreferences.saveString(TRACK_HISTORY,trackListHistory)
        } else {
            if (trackListHistory.size < 10) {
                trackListHistory.add(0, track)
                sharedPreferences.saveString(TRACK_HISTORY,trackListHistory)
            } else {
                trackListHistory.removeAt(9)
                trackListHistory.add(0, track)
                sharedPreferences.saveString(TRACK_HISTORY,trackListHistory)
            }
        }
    }
    fun clearHistory(){
        trackListHistory.clear()
        sharedPreferences.clear(TRACK_HISTORY)
    }
    fun getHistory():ArrayList<Track>{
        val jsonString = sharedPreferences.getString(TRACK_HISTORY)
        val json = GsonBuilder().create()
        val itemType = object : TypeToken<ArrayList<Track>>() {}.type
        trackListHistory = json.fromJson<ArrayList<Track>>(jsonString, itemType) ?: arrayListOf()
        return trackListHistory
    }
    companion object{
        private const val TRACK_HISTORY = "track_history"
    }
}
