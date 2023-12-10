package com.example.playlistmaker.settings.data

interface SettingsRepository {
    fun updateThemeSetting(isDarkTheme: Boolean)
    fun isDarkTheme(): Boolean
}