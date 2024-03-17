package com.example.playlistmaker.library.ui.activity

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.example.playlistmaker.library.ui.view_model.NewPlaylistState
import com.example.playlistmaker.library.ui.view_model.NewPlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class NewPlaylistFragment : Fragment() {
    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NewPlaylistViewModel by viewModel()
    private var uriString = ""


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
        setArrowBackListener()
        setPictureUploaderListener()
        setEditTextListeners()
        setNewPlaylistButtonListener()
    }

    private fun setNewPlaylistButtonListener() {
        //слушатель кнопки создать плейлист
        binding.createNewPlaylistButton.setOnClickListener() {
            viewModel.createPlayList(
                binding.playlistName.text.toString(),
                binding.playlistDescription.text.toString(),
                uriString
            )
            Toast.makeText(
                requireContext(),
                getString(R.string.playlist_created, binding.playlistName.text.toString()),
                Toast.LENGTH_LONG
            )
                .show()
            findNavController().popBackStack()
        }
    }

    private fun setEditTextListeners() {
        //слушатель поля Name
        val textWatcherName = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.createNewPlaylistButton.isEnabled = s.toString().isNotEmpty()
                viewModel.isEditing = true
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        textWatcherName.let { binding.playlistName.addTextChangedListener(it) }

        //слушатель поля Description
        val textWatcherDescription = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.isEditing = true
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        textWatcherDescription.let { binding.playlistName.addTextChangedListener(it) }
    }

    private fun setPictureUploaderListener() {
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    binding.playlistCover.setImageURI(uri)
                    uriString = uri.toString()
                }
            }
        binding.playlistCover.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun render(state: NewPlaylistState) {
        when (state) {
            NewPlaylistState.Empty -> {
                binding.createNewPlaylistButton.isEnabled = false
                binding.createNewPlaylistButton.backgroundTintList =
                    ColorStateList.valueOf(requireContext().getColor(R.color.grey))
            }

            NewPlaylistState.Editing -> {
                binding.createNewPlaylistButton.isEnabled = true
                binding.createNewPlaylistButton.backgroundTintList =
                    ColorStateList.valueOf(requireContext().getColor(R.color.blue))
            }
        }
    }

    private fun setArrowBackListener() {
        binding.arrowBack.setOnClickListener() {
            if (viewModel.isEditing) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.finish_playlist_creating))
                    .setMessage(getString(R.string.data_save_warning))
                    .setNeutralButton(getString(R.string.Cancel)) { dialog, which ->
                    }
                    .setPositiveButton(getString(R.string.finish)) { dialog, which ->
                        findNavController().popBackStack()
                    }
                    .show()
            } else {
                findNavController().popBackStack()
            }
        }
    }

}