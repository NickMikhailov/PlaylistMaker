package com.example.playlistmaker.main.domain

import android.app.Application
import com.example.playlistmaker.creator.Creator

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Creator.putApplication(this)
        val settingsInteractor = Creator.provideSettingsInteractor()
        settingsInteractor.updateThemeSetting(settingsInteractor.isDarkTheme())
    }
}