package com.example.playlistmaker.settings.domain.impl

import android.app.Application
import android.content.Context
import com.example.playlistmaker.main.domain.App
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.settings.domain.model.ThemeSettings

class SettingsInteractorImpl(val context: Context): SettingsInteractor {
    override fun getThemeSettings(): ThemeSettings {
        TODO("Not yet implemented")
    }

    override fun updateThemeSetting(isChecked: Boolean) {
        TODO("Not yet implemented")
//        (context as App).switchTheme(isChecked) //недопустимое приведение типа
    }
}