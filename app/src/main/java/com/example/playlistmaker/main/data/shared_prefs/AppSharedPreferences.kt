package com.example.playlistmaker.main.data.shared_prefs

import android.app.Application
import android.content.Context
import com.google.gson.Gson

class AppSharedPreferences(private val application: Application) {
    fun clear(key: String) {
        val sharedPreferences = (application as Context).getSharedPreferences(key, Context.MODE_PRIVATE)
        sharedPreferences.edit().remove(key).apply()
    }
    fun getBoolean(key:String):Boolean{
        val sharedPreferences = (application as Context).getSharedPreferences(key, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(key,false)
    }
    fun saveBoolean(key:String, value:Boolean){
        val sharedPreferences = (application as Context).getSharedPreferences(key, Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(key, value).apply()
    }
    fun getString(key:String):String?
    {
        val sharedPreferences = (application as Context).getSharedPreferences(key, Context.MODE_PRIVATE)
        return sharedPreferences.getString(key,"")
    }
    fun saveString(key:String, value:Any){
        val sharedPreferences = (application as Context).getSharedPreferences(key, Context.MODE_PRIVATE)
        val json = Gson()
        val jsonString = json.toJson(value)
        sharedPreferences
            .edit()
            .putString(key, jsonString)
            .apply()
    }
}