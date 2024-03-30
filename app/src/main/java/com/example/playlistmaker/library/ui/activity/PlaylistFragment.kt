package com.example.playlistmaker.library.ui.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.library.ui.adapter.PlaylistsListViewAdapter
import com.example.playlistmaker.library.ui.view_model.PlaylistState
import com.example.playlistmaker.library.ui.view_model.PlaylistViewModel
import com.example.playlistmaker.player.domain.models.Track
import com.example.playlistmaker.main.domain.models.DateTimeUtil
import com.example.playlistmaker.main.domain.models.GrammarUtil
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

class PlaylistFragment : Fragment() {
    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlaylistViewModel by viewModel()
    private var playlistTrackListAdapter = TrackListAdapter(ArrayList())
    private var isClickAllowed = true
    private lateinit var playlist: Playlist

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val playlistId = arguments?.getString(getString(R.string.playlist_id))
        viewModel.updatePlaylist(playlistId)
        binding.PlaylistTrackListView.adapter = playlistTrackListAdapter

        viewModel.observeState().observe(viewLifecycleOwner, ::render)
        viewModel.observeStartPlayerEvent().observe(viewLifecycleOwner, ::showPlayer)
        viewModel.observeEditPlaylistEvent().observe(viewLifecycleOwner, ::editPlaylist)

        val bottomSheetTrackListBehavior = BottomSheetBehavior.from(binding.bottomSheetTracks)
        bottomSheetTrackListBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        setListeners(playlistId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        isClickAllowed = true
    }

    private fun setListeners(playlistId: String?) {
        //слушатель кнопки назад
        binding.arrowBack.setOnClickListener {
            findNavController().popBackStack()
        }
        //слушатель нажатия на трек в плейлисте
        playlistTrackListAdapter.setOnItemClickListener { position ->
            if (clickDebounce()) {
                viewModel.showPlayer(playlistTrackListAdapter.getTrack(position))
            }
        }
        //слушатель долгого нажатия на трек в плейлисте
        playlistTrackListAdapter.setOnItemLongClickListener { position ->
            val selectedTrack = playlistTrackListAdapter.getTrack(position)
            val dialog = MaterialAlertDialogBuilder(requireContext())
                .setMessage(getString(R.string.delete_track_confirmation))
                .setNeutralButton(getString(R.string.no)) { dialog, which -> dialog.dismiss() }
                .setPositiveButton(getString(R.string.yes)) { _, _ ->
                    viewModel.deleteTrackFromPlaylist(playlistId, selectedTrack)
                }
                .show()
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(requireContext().getColor(R.color.blue))
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(requireContext().getColor(R.color.blue))

        }
        //слушатель клика на иконку поделиться
        binding.shareIcon.setOnClickListener {
            sharePlaylist(playlistId)
        }
        //слушатель клика на иконку меню
        binding.menuIcon.setOnClickListener {
            val bottomSheetMenuBehavior = BottomSheetBehavior.from(binding.bottomSheetEditPlaylist)
            bottomSheetMenuBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            bottomSheetMenuBehavior.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_HIDDEN -> {
                            binding.overlay.isVisible = false
                            binding.bottomSheetTracks.isVisible = true
                        }

                        else -> {
                            binding.overlay.isVisible = true
                            binding.bottomSheetTracks.isVisible = false
                        }
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })
        }
        //слушатель клика на пункт меню поделиться
        binding.sharePlaylist.setOnClickListener {
            sharePlaylist(playlistId)
        }
        //слушатель клика на пункт меню удалить плейлист
        binding.deletePlaylist.setOnClickListener {
            binding.bottomSheetEditPlaylist.isVisible = false
            val dialog = MaterialAlertDialogBuilder(requireContext())
                .setMessage(getString(R.string.delete_playlist_confirmation, playlist.name))
                .setNeutralButton(getString(R.string.no)) { dialog, which ->
                    binding.bottomSheetEditPlaylist.isVisible = true
                    dialog.dismiss() }
                .setPositiveButton(getString(R.string.yes)) { _, _ ->
                    viewModel.deletePlaylist(playlistId)
                }
                .show()
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(requireContext().getColor(R.color.blue))
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(requireContext().getColor(R.color.blue))

        }
        //слушатель клика на пункт меню редактировать плейлист
        binding.editPlaylist.setOnClickListener {
            viewModel.editPlaylist(playlist)
        }
    }

    private fun render(state: PlaylistState) {
        when (state) {
            is PlaylistState.Empty -> {
                binding.PlaylistTrackListView.isVisible = false
            }
            is PlaylistState.EmptyList -> {
                binding.PlaylistTrackListView.isVisible = false
                showContent(state.playlist, listOf())
            }
            is PlaylistState.Content -> {
                binding.PlaylistTrackListView.isVisible = true
                showContent(state.playlist, state.tracklist)
            }

            is PlaylistState.PlaylistDeleted -> {
                findNavController().popBackStack()
            }
        }
    }
    private fun sharePlaylist(playlistId: String?) {
        if (playlistTrackListAdapter.trackList.isNotEmpty()) {
            viewModel.sharePlaylist(playlistId)
        } else {
            MaterialAlertDialogBuilder(requireContext())
                .setMessage(getString(R.string.share_empty_list_warning))
                .setPositiveButton(R.string.ok) { dialog, _ -> dialog.dismiss() }
                .show()
        }


    }
    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun showContent(playlist: Playlist, trackList: List<Track>) {
        this.playlist = playlist
        //обложка
        try {
            val file = File(playlist.coverName)
            val inputStream = FileInputStream(file)
            val image = BitmapFactory.decodeStream(inputStream)
            binding.playlistCover.setImageBitmap(image)
            binding.arrowBack.visibility = View.GONE
            binding.emptyView.visibility = View.GONE
        } catch (e: Exception) {
            binding.playlistCover.setImageResource(R.drawable.cover_placeholder)
            binding.arrowBack.visibility = View.VISIBLE
            binding.emptyView.visibility = View.VISIBLE
        }
        //название и описание
        binding.playlistName.text = playlist.name
        binding.playlistDescription.text = playlist.description
        // продолжительность и количество треков
        binding.playlistDuration.text = DateTimeUtil.convertMillisToMinutes(playlist.duration)
        binding.playlistTrackCount.text =
            "${playlist.trackCount}${GrammarUtil.getEnding(playlist.trackCount,"трек","трека","треков")}"
        //список треков
        playlistTrackListAdapter.trackList.clear()
        playlistTrackListAdapter.trackList.addAll(trackList)
        playlistTrackListAdapter.notifyDataSetChanged()
        //текущий плейлист (в BottomSheetEditPlaylist)
        binding.currentPlaylist.adapter = PlaylistsListViewAdapter(listOf(playlist))
        val bottomSheetEditPlaylistBehavior =
            BottomSheetBehavior.from(binding.bottomSheetEditPlaylist)
        bottomSheetEditPlaylistBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }
    private fun showPlayer(track: Track) {
        val bundle = Bundle()
        bundle.putString(getString(R.string.json_track), Gson().toJson(track))
        findNavController().navigate(R.id.action_PlaylistFragment_to_PlayerFragment, bundle)
    }
    private fun editPlaylist(playlist: Playlist) {
        val bundle = Bundle()
        bundle.putString(getString(R.string.playlist_id), Gson().toJson(playlist.id))
        findNavController().navigate(R.id.action_PlaylistFragment_to_EditPlaylistFragment, bundle)
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