package com.example.playlistmaker.settings.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.R
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.sharing.SharingInteractor
import java.lang.Exception

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
) : ViewModel() {
    private val settingsStateLiveData = MutableLiveData<SettingsState>()
    fun observeState(): LiveData<SettingsState> = settingsStateLiveData
    private fun renderState(state: SettingsState) {
        this.settingsStateLiveData.postValue(state)
    }
    fun shareApp() {
        try {
            sharingInteractor.shareApp()
            renderState(SettingsState.Success)
        } catch (ex: Exception) {
            renderState(SettingsState.Error(R.string.err_msg_to_share))
        }
    }
    fun openTerms() {
        try {sharingInteractor.openTerms()
            renderState(SettingsState.Success)
        }
        catch (ex: Exception){
            renderState(SettingsState.Error(R.string.err_msg_to_open_url))
        }
    }
    fun openSupport() {
        try {
            sharingInteractor.openSupport()
            renderState(SettingsState.Success)
        }
        catch (ex: Exception){
            renderState(SettingsState.Error(R.string.err_msg_to_send_email))
        }
    }
    fun updateThemeSettings(isDarkMode: Boolean) {
        settingsInteractor.updateThemeSetting(isDarkMode)
    }
    fun isDarkTheme(): Boolean {
        return settingsInteractor.isDarkTheme()
    }
}


