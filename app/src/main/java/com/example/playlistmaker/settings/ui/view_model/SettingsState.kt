package com.example.playlistmaker.settings.ui.view_model

sealed interface SettingsState {
    object Success:SettingsState
    data class Error (val errorMessage:Int):SettingsState
}