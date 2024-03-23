package com.example.playlistmaker.library.ui.activity

import android.app.AlertDialog
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.library.ui.adapter.PlaylistsListViewAdapter
import com.example.playlistmaker.library.ui.view_model.PlaylistState
import com.example.playlistmaker.library.ui.view_model.PlaylistViewModel
import com.example.playlistmaker.player.domain.models.Track
import com.example.playlistmaker.search.domain.models.DateTimeUtil
import com.example.playlistmaker.search.ui.TrackListAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileInputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistFragment : Fragment() {
    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlaylistViewModel by viewModel()
    private var playlistTrackListAdapter = TrackListAdapter(ArrayList())
    private var isClickAllowed = true

    companion object {
        private const val KEY_TRACK = "jsonTrack"
        private const val PLAYLIST_ID = "playlistId"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.PlaylistTrackListView.adapter = playlistTrackListAdapter
        val playlistId = arguments?.getString(PLAYLIST_ID)

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetTracks)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        viewModel.getPlaylist(playlistId)
        setListeners(playlistId)

        viewModel.observeState().observe(viewLifecycleOwner, ::render)
        viewModel.observeStartPlayerEvent().observe(viewLifecycleOwner, ::showPlayer)
    }

    private fun setListeners(playlistId: String?) {
        playlistTrackListAdapter.setOnItemClickListener { position ->
            if (clickDebounce()) {
                viewModel.showPlayer(playlistTrackListAdapter.getTrack(position))
            }
        }
        playlistTrackListAdapter.setOnItemLongClickListener { position ->
            val selectedTrack = playlistTrackListAdapter.getTrack(position)
            MaterialAlertDialogBuilder(requireContext())
                .setMessage(getString(R.string.delete_track_confirmation))
                .setNeutralButton(getString(R.string.no)) { dialog, which -> dialog.dismiss() }
                .setPositiveButton(getString(R.string.yes)) { _, _ ->
                    viewModel.removeTrackFromPlaylist(playlistId, selectedTrack)
                }
                .show()
        }
        binding.shareIcon.setOnClickListener{
            if (playlistTrackListAdapter.trackList.isNotEmpty()){
                viewModel.sharePlaylist(playlistId)
            }
            else{
                MaterialAlertDialogBuilder(requireContext())
                    .setMessage(getString(R.string.share_empty_list_warning))
                    .setPositiveButton(R.string.ok) {dialog,_ -> dialog.dismiss()}
                    .show()
            }
        }

        binding.menuIcon.setOnClickListener{
            val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetEditPlaylist)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }

    }

    private fun render(state: PlaylistState) {
        when (state) {
            is PlaylistState.Empty -> {
                binding.PlaylistTrackListView.isVisible = false
            }

            is PlaylistState.Content -> {
                binding.PlaylistTrackListView.isVisible = true
                showContent(state.playlist, state.tracklist)
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

    private fun showContent(playlist: Playlist, trackList: List<Track>) {
        try {
            val file = File(playlist.coverName)
            val inputStream = FileInputStream(file)
            val image = BitmapFactory.decodeStream(inputStream)
            binding.playlistCover.setImageBitmap(image)
        } catch (e: Exception) {
            binding.playlistCover.setImageResource(R.drawable.cover_placeholder)
        }
        binding.playlistName.text = playlist.name
        binding.playlistDescription.text = playlist.description
        binding.playlistDuration.text = SimpleDateFormat(
            DateTimeUtil.MM,
            Locale.getDefault()
        ).format(playlist.duration) + getDurationEnding(playlist.duration)
        binding.playlistTrackCount.text =
            "${playlist.trackCount}${getTrackEnding(playlist.trackCount)}"
        playlistTrackListAdapter.trackList.clear()
        playlistTrackListAdapter.trackList.addAll(trackList)
        playlistTrackListAdapter.notifyDataSetChanged()
        val currentPlaylist = listOf<Playlist>(playlist)
        val currentTrackListAdapter = PlaylistsListViewAdapter(currentPlaylist)
        binding.currentPlaylist.adapter = currentTrackListAdapter
        val bottomSheetEditPlaylistBehavior = BottomSheetBehavior.from(binding.bottomSheetEditPlaylist)
        bottomSheetEditPlaylistBehavior.state = BottomSheetBehavior.STATE_HIDDEN


    }

    private fun showPlayer(track: Track) {
        val bundle = Bundle()
        bundle.putString(KEY_TRACK, Gson().toJson(track))
        findNavController().navigate(R.id.action_PlaylistFragment_to_PlayerFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        isClickAllowed = true
    }

    private fun getTrackEnding(count: Int): String {
        return when {
            (count % 100) in 10..20 || (count % 10) in 5..9 || (count % 10) == 0 -> " треков"
            (count % 10) == 1 -> " трек"
            else -> " трека"
        }
    }

    private fun getDurationEnding(duration: Long): String {
        return when {
            (duration % 100) in 10..20 || (duration % 10) in 5..9 || (duration % 10) == 0L -> " минут"
            (duration % 10) == 1L -> " минута"
            else -> " минуты"
        }
    }
}