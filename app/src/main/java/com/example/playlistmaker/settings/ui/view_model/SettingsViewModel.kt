package com.example.playlistmaker.settings.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
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
            renderState(SettingsState.Error(ERROR_SHARE_LINK))
        }
    }

    fun openTerms() {
        try {sharingInteractor.openTerms()
            renderState(SettingsState.Success)
        }
        catch (ex: Exception){
            renderState(SettingsState.Error(ERROR_OPEN_LINK))
        }
    }

    fun openSupport() {
        try {
            sharingInteractor.openSupport()
            renderState(SettingsState.Success)
        }
        catch (ex: Exception){
            renderState(SettingsState.Error(ERROR_SEND_EMAIL))
        }

    }

    fun updateThemeSettings(isDarkMode: Boolean) {
        settingsInteractor.updateThemeSetting(isDarkMode)
    }

    companion object {
        fun factory(
            sharingInteractor: SharingInteractor,
            settingsInteractor: SettingsInteractor
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(sharingInteractor, settingsInteractor)
            }
        }
        private const val ERROR_SEND_EMAIL = "Не удалось отправить сообщение!"
        private const val ERROR_OPEN_LINK = "Не удалось открыть ссылку!"
        private const val ERROR_SHARE_LINK = "Не удалось поделиться ссылкой!"
    }
}


