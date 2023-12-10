package com.example.playlistmaker.settings.domain

interface SettingsInteractor {
    fun updateThemeSetting(isDarkMode: Boolean)
    fun isDarkTheme():Boolean
}