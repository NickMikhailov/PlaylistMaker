package com.example.playlistmaker.library.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.library.ui.adapter.PlaylistsGridViewAdapter
import com.example.playlistmaker.library.ui.view_model.PlaylistsState
import com.example.playlistmaker.library.ui.view_model.PlaylistsViewModel
import com.example.playlistmaker.search.domain.models.DateTimeUtil
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlaylistsViewModel by viewModel()
    private lateinit var playlistGridAdapter: PlaylistsGridViewAdapter
    private var isClickAllowed = true
    companion object {
        fun newInstance() = PlaylistsFragment()
        const val COLUMN_COUNT = 2
        const val PLAYLIST_ID = "playlistId"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.observeState().observe(viewLifecycleOwner, ::render)
        viewModel.observeShowPlaylistEvent().observe(viewLifecycleOwner, ::showPlaylist)

        binding.newPlaylistButton.setOnClickListener{
            findNavController().navigate(R.id.action_LibraryFragment_to_newPlaylistFragment)
        }
        viewModel.getPlaylists()
        binding.playlistsGrid.layoutManager = GridLayoutManager(requireContext(), COLUMN_COUNT)

    }

    private fun showPlaylist(playlistId: Long?) {
        val bundle = Bundle()
        bundle.putString(PLAYLIST_ID, Gson().toJson(playlistId))
        findNavController().navigate(R.id.action_LibraryFragment_to_playlistFragment, bundle)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        isClickAllowed = true
    }

    private fun render(state: PlaylistsState) {
        when (state) {
            is PlaylistsState.Empty -> {
                binding.placeholder.isVisible = true
                binding.playlistsGrid.isVisible = false
            }
            is PlaylistsState.Content -> {
                binding.placeholder.isVisible = false
                binding.playlistsGrid.isVisible = true
                playlistGridAdapter = PlaylistsGridViewAdapter(state.playlists)
                binding.playlistsGrid.adapter = playlistGridAdapter
                playlistGridAdapter.setOnItemClickListener{ position ->
                    if (clickDebounce()){
                        viewModel.showPlaylist(position)
                    }
                }
            }
        }
    }
    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewLifecycleOwner.lifecycleScope.launch {
                delay(DateTimeUtil.CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }
}