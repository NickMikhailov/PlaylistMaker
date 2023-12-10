package com.example.playlistmaker.main.domain

import android.app.Application
import com.example.playlistmaker.settings.domain.SettingsInteractor
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import dataModule
import interactorModule
import repositoryModule
import viewModelModule
import org.koin.android.ext.android.inject
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(dataModule, repositoryModule, interactorModule, viewModelModule)
        }
        val settingsInteractor: SettingsInteractor by inject()
        settingsInteractor.updateThemeSetting(settingsInteractor.isDarkTheme())
    }
}