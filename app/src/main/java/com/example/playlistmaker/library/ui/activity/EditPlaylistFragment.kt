package com.example.playlistmaker.library.ui.activity

import android.app.AlertDialog
import android.graphics.BitmapFactory
import android.os.Bundle
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
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.library.ui.view_model.EditPlaylistState
import com.example.playlistmaker.library.ui.view_model.EditPlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileInputStream
import java.lang.Exception

class EditPlaylistFragment() : Fragment() {
    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EditPlaylistViewModel by viewModel()
    private var uriString = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.observeState().observe(viewLifecycleOwner, ::render)
        val playlistId = arguments?.getString(getString(R.string.playlist_id))
        viewModel.getPlaylist(playlistId)
        renderInit()
        viewModel.isEditing = false
        setArrowBackListener()
        setListeners(playlistId)
    }

    private fun setListeners(playlistId:String?) {
        //слушатель поля Name
        val textWatcherName = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.isEditing = true
                binding.createNewPlaylistButton.isEnabled = s.toString().isNotEmpty()
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        textWatcherName.let { binding.playlistName.addTextChangedListener(it) }
        //слушатель загрузки картинки
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
        //слушатель кнопки обновить лист
        binding.createNewPlaylistButton.setOnClickListener() {
            viewModel.updatePlayList(
                playlistId,
                binding.playlistName.text.toString(),
                binding.playlistDescription.text.toString(),
                uriString
            )
            Toast
                .makeText(
                    requireContext(),
                    getString(R.string.playlist_updated, binding.playlistName.text.toString()),
                    Toast.LENGTH_LONG
                )
                .show()
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun renderInit() {
        binding.newPlayListHeader.text = getString(R.string.edit)
        binding.createNewPlaylistButton.text = getString(R.string.update)
    }

    private fun render(state: EditPlaylistState?) {
        when (state) {
            is EditPlaylistState.Empty -> {
            }

            is EditPlaylistState.Content -> {
                showContent(state.playlist)
            }

            else -> {}
        }
    }

    private fun showContent(playlist: Playlist) {
        try {
            val file = File(playlist.coverName)
            val inputStream = FileInputStream(file)
            val image = BitmapFactory.decodeStream(inputStream)
            binding.playlistCover.setImageBitmap(image)
        } catch (e: Exception) {
            binding.playlistCover.setImageResource(R.drawable.cover_placeholder)
        }
        //название и описание
        binding.playlistName.setText(playlist.name)
        binding.playlistDescription.setText(playlist.description)
    }
    private fun setArrowBackListener() {
        binding.arrowBack.setOnClickListener() {
            if (viewModel.isEditing) {
                val dialog = MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.finish_playlist_creating))
                    .setMessage(getString(R.string.data_save_warning))
                    .setNeutralButton(getString(R.string.cancel)) { dialog, which ->
                    }
                    .setPositiveButton(getString(R.string.finish)) { dialog, which ->
                        findNavController().popBackStack()
                    }
                    .show()
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(requireContext().getColor(R.color.blue))
                dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(requireContext().getColor(R.color.blue))
            } else {
                findNavController().popBackStack()
            }
        }
    }
}