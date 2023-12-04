package com.example.playlistmaker.settings.data.impl

import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.settings.data.SettingsRepository

class SettingsRepositoryImpl: SettingsRepository {

    private val sharedPreferences = Creator.provideSharedPreferences()
    override fun updateThemeSetting(isDarkTheme: Boolean) {
        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            sharedPreferences.saveBoolean(IS_DARK_THEME, true)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            sharedPreferences.saveBoolean(IS_DARK_THEME, false)
        }
    }

    override fun isDarkTheme(): Boolean {
        return sharedPreferences.getBoolean(IS_DARK_THEME)
    }
    companion object{
        private const val IS_DARK_THEME = "isDarkMode"
    }
}