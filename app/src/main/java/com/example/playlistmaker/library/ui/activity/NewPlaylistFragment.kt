package com.example.playlistmaker.library.ui.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
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

        binding.createNewPlaylistButton.isEnabled = binding.playlistName.text.toString().isNotEmpty()

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
            Toast
                .makeText(
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
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
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
        textWatcherDescription.let { binding.playlistDescription.addTextChangedListener(it) }
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
            viewModel.isEditing = true
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

    private fun setArrowBackListener() {
        binding.arrowBack.setOnClickListener() {
            if (viewModel.isEditing) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.finish_playlist_creating))
                    .setMessage(getString(R.string.data_save_warning))
                    .setNeutralButton(getString(R.string.cancel)) { dialog, which ->
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