package com.example.playlistmaker.settings.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.settings.ui.view_model.SettingsState
import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private val sharingInteractor = Creator.provideSharingInteractor()
    private val settingsInteractor = Creator.provideSettingsInteractor()
    private val viewModel by lazy {
        ViewModelProvider(this, SettingsViewModel.factory(sharingInteractor,settingsInteractor))[SettingsViewModel::class.java]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setListeners()

        binding.themeSwitcher.isChecked = settingsInteractor.isDarkTheme()

        viewModel.observeState().observe(this){
            render(it)
        }
    }

    private fun render(state: SettingsState?) {
        when(state){
            is SettingsState.Success -> {
            }
            is SettingsState.Error -> {
                Toast.makeText(applicationContext, state.errorMessage, Toast.LENGTH_LONG)
                    .show()
            }
            null -> {}
        }
    }
    private fun setListeners() {
        binding.arrowBack.setOnClickListener {
            finish()
        }
        binding.shareIcon.setOnClickListener {
            viewModel.shareApp()
        }
        binding.supportIcon.setOnClickListener {
            viewModel.openSupport()
        }

        binding.agreementIcon.setOnClickListener {
            viewModel.openTerms()
        }

        binding.themeSwitcher.setOnCheckedChangeListener { _, isDarkMode ->
        viewModel.updateThemeSettings(isDarkMode)
        }
    }
}