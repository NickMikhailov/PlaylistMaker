package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

class SearchHistory (private val sharedPreferences: SharedPreferences){
    private var trackListHistory = ArrayList<Track>()
    fun addToHistory(track: Track) {
        if (trackListHistory.contains(track)) {
            trackListHistory.remove(track)
            trackListHistory.add(0,track)
            saveToSharedPrefs()
        } else {
            if (trackListHistory.size < 10) {
                trackListHistory.add(0, track)
                saveToSharedPrefs()
            } else {
                trackListHistory.removeAt(9)
                trackListHistory.add(0, track)
                saveToSharedPrefs()
            }
        }
    }
    fun clearHistory(){
        trackListHistory.clear()
        sharedPreferences.edit().remove("track_history").apply()
    }
    fun getHistory():ArrayList<Track>{
        val jsonString = sharedPreferences.getString("track_history","")
        val json = GsonBuilder().create()
        val itemType = object : TypeToken<ArrayList<Track>>() {}.type
        trackListHistory = json.fromJson<ArrayList<Track>>(jsonString, itemType) ?: arrayListOf()
        return trackListHistory
    }

    private fun saveToSharedPrefs(){
        val json = Gson()
        val jsonString = json.toJson(trackListHistory)
        sharedPreferences
            .edit()
            .putString("track_history", jsonString)
            .apply()
    }
}
