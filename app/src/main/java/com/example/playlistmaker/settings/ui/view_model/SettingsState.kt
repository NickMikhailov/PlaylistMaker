package com.example.playlistmaker.settings.ui.view_model

import android.content.Intent

sealed interface SettingsState {
    object Success:SettingsState
    data class Error (val errorMessage:String):SettingsState
}