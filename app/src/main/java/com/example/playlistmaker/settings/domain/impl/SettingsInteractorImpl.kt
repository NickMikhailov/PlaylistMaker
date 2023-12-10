package com.example.playlistmaker.settings.domain.impl

import com.example.playlistmaker.settings.data.SettingsRepository
import com.example.playlistmaker.settings.domain.SettingsInteractor

class SettingsInteractorImpl(
    private val settingsRepository: SettingsRepository
): SettingsInteractor {

    override fun updateThemeSetting(isDarkMode: Boolean) {
        settingsRepository.updateThemeSetting(isDarkMode)
    }
    override fun isDarkTheme(): Boolean {
        return settingsRepository.isDarkTheme()
    }
}