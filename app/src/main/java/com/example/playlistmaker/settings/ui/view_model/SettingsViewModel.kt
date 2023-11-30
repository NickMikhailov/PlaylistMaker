package com.example.playlistmaker.settings.ui.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.R
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.sharing.SharingInteractor
import java.lang.Exception

class SettingsViewModel(
    private final val application: Application,
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
) : AndroidViewModel(application) {
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
            renderState(SettingsState.Error(application.getString(R.string.err_msg_to_share)))
        }
    }

    fun openTerms() {
        try {sharingInteractor.openTerms()
            renderState(SettingsState.Success)
        }
        catch (ex: Exception){
            renderState(SettingsState.Error(application.getString(R.string.err_msg_to_open_url)))
        }
    }

    fun openSupport() {
        try {
            sharingInteractor.openSupport()
            renderState(SettingsState.Success)
        }
        catch (ex: Exception){
            renderState(SettingsState.Error(application.getString(R.string.err_msg_to_send_email)))
        }

    }

    fun updateThemeSettings(isChecked: Boolean) {
        settingsInteractor.updateThemeSetting(isChecked)
    }

    companion object {
        fun factory(
            sharingInteractor: SharingInteractor,
            settingsInteractor: SettingsInteractor
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application,
                    sharingInteractor,
                    settingsInteractor
                )
            }
        }
    }
}


