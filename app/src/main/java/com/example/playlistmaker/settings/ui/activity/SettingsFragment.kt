package com.example.playlistmaker.settings.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.settings.ui.view_model.SettingsState
import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModel()

/*    companion object {
        fun newInstance() = SettingsFragment()
    }*/

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        _binding = FragmentSettingsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        binding.themeSwitcher.isChecked = viewModel.isDarkTheme()
        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun render(state: SettingsState?) {
        when (state) {
            is SettingsState.Success -> {
            }

            is SettingsState.Error -> {
                Toast.makeText(requireContext(), getString(state.errorMessage), Toast.LENGTH_LONG)
                    .show()
            }

            null -> {}
        }
    }

    private fun setListeners() {
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