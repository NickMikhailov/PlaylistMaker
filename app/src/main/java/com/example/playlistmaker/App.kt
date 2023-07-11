package com.example.playlistmaker

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
        switchTheme(getCurrentTheme())
        sharedPreferences.registerOnSharedPreferenceChangeListener { sharedPrefs, key ->
            switchTheme(getCurrentTheme())
        }
    }

    fun getCurrentTheme(): Boolean {
        return sharedPreferences.getBoolean("isDarkMode", false)
    }

    fun switchTheme(isDarkMode: Boolean) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            sharedPreferences.edit().putBoolean("isDarkMode", true).apply()
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            sharedPreferences.edit().putBoolean("isDarkMode", false).apply()
        }
    }
}