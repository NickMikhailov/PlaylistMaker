package com.example.playlistmaker.settings.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.settings.ui.view_model.SettingsState
import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    private val viewModel: SettingsViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setListeners()

        binding.themeSwitcher.isChecked = viewModel.isDarkTheme()

        viewModel.observeState().observe(this){
            render(it)
        }
    }

    private fun render(state: SettingsState?) {
        when(state){
            is SettingsState.Success -> {
            }
            is SettingsState.Error -> {
                Toast.makeText(applicationContext, getString(state.errorMessage), Toast.LENGTH_LONG)
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