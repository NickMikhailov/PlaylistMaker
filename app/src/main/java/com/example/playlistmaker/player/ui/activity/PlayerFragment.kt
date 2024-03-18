package com.example.playlistmaker.player.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.library.ui.adapter.PlaylistsListViewAdapter
import com.example.playlistmaker.player.domain.models.Track
import com.example.playlistmaker.player.ui.view_model.BottomSheetState
import com.example.playlistmaker.player.ui.view_model.PlayerState
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel
import com.example.playlistmaker.search.domain.models.DateTimeUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerFragment : Fragment() {

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    private lateinit var track: Track
    private lateinit var playlistListViewAdapter: PlaylistsListViewAdapter
    private val viewModel: PlayerViewModel by viewModel {
        parametersOf(track)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root}
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val jsonString = arguments?.getString(KEY_TRACK)
        jsonString?.let {
            val gson = Gson()
            track = gson.fromJson(jsonString, Track::class.java)
            renderTrack(track)
            viewModel.setTrackFavoriteState(track)
        }

        setListeners()
        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.observeBottomSheetState().observe(viewLifecycleOwner) {
            renderBottomSheet(it)
        }

        binding.PlaylistsListView.adapter = PlaylistsListViewAdapter(ArrayList())
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_TRACK, Gson().toJson(track))
    }

    private fun setListeners() {
        binding.arrowBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.playPauseButton.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }
        binding.addToFavoriteButton.setOnClickListener {
            viewModel.onFavoriteClicked(track)
        }
        binding.addToPlayListButton.setOnClickListener {
            viewModel.onPlaylistClicked(track)
        }
        binding.newPlaylistButton.setOnClickListener() {
            findNavController().navigate(R.id.action_playerFragment_to_newPlaylistFragment)
        }
    }

    private fun renderTrack(track: Track) {
        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.trackTime.text = DateTimeUtil.ZERO
        binding.listDurationValue.text = track.trackTime
        binding.listCollectionValue.text = track.collectionName
        binding.listYearValue.text = track.releaseDate.substring(FIRST_SYMBOL, FOURTH_SYMBOL)
        binding.listGenreValue.text = track.primaryGenreName
        binding.listCountryValue.text = track.country
        Glide.with(this)
            .load(track.artworkUrl500)
            .placeholder(R.drawable.cover_placeholder)
            .fitCenter()
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.corner_radius_medium_8)))
            .into(binding.trackCover)
        setFavoriteButtonState()
    }

    private fun render(state: PlayerState) {
        when (state) {
            is PlayerState.Default -> {
                setDefaultState()
            }

            is PlayerState.Prepared -> {
                setPlayerPreparedState()
            }

            is PlayerState.Paused -> {
                setPausedState(state)
            }

            is PlayerState.Playing -> {
                setPlayingState(state)
            }

        }
        setFavoriteButtonState()
    }

    private fun renderBottomSheet(state: BottomSheetState) {
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetPlaylists)

        when (state) {
            is BottomSheetState.Default -> {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }

            is BottomSheetState.PlaylistsEditing -> {
                binding.bottomSheetPlaylists.isVisible = true
                binding.overlay.isVisible = true
                playlistListViewAdapter = PlaylistsListViewAdapter(state.playlists)
                binding.PlaylistsListView.adapter = playlistListViewAdapter
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                playlistListViewAdapter.setOnItemClickListener { position ->
                    viewModel.addTrackToPlaylist(position, track)
                }
            }

            is BottomSheetState.PlaylistEditingComplete -> {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                binding.overlay.isVisible = true
                val toastText = if (state.trackAdded) {
                    getString(R.string.added_to_playlist, state.playlistName)
                } else {
                    getString(R.string.track_already_added, state.playlistName)
                }
                Toast.makeText(requireContext(), toastText, Toast.LENGTH_SHORT).show()
            }
        }
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.isVisible = false
                    }
                    else -> {
                        binding.overlay.isVisible = true
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
    }

    private fun setDefaultState() {
        binding.playPauseButton.isEnabled = false
        binding.playPauseButton.setImageResource(R.drawable.play_button_inactive)
    }

    private fun setPlayerPreparedState() {
        binding.playPauseButton.isEnabled = true
        binding.playPauseButton.setImageResource(R.drawable.play_button)
        binding.trackTime.text = DateTimeUtil.ZERO
    }

    private fun setPausedState(state: PlayerState) {
        binding.playPauseButton.isEnabled = true
        binding.playPauseButton.setImageResource(R.drawable.play_button)
        binding.trackTime.text = state.progress
    }

    private fun setPlayingState(state: PlayerState) {
        binding.playPauseButton.isEnabled = true
        binding.playPauseButton.setImageResource(R.drawable.pause_button)
        binding.trackTime.text = state.progress
    }

    private fun setFavoriteButtonState() {
        if (track.isFavorite) {
            binding.addToFavoriteButton.setImageResource(R.drawable.add_to_favorite_button_true)
        } else {
            binding.addToFavoriteButton.setImageResource(R.drawable.add_to_favorite_button_false)
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    companion object {
        private const val KEY_TRACK = "jsonString"
        private const val FIRST_SYMBOL = 0
        private const val FOURTH_SYMBOL = 4
    }
}