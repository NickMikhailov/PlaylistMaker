package com.example.playlistmaker.library.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavoritesBinding
import com.example.playlistmaker.library.ui.view_model.FavoritesState
import com.example.playlistmaker.library.ui.view_model.FavoritesViewModel
import com.example.playlistmaker.player.domain.models.Track
import com.example.playlistmaker.main.domain.models.DateTimeUtil
import com.example.playlistmaker.search.ui.TrackListAdapter
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FavoritesViewModel by viewModel()
    private var favoritesTrackListAdapter = TrackListAdapter(ArrayList())
    private var isClickAllowed = true

    companion object {
        fun newInstance() = FavoritesFragment()
        private const val KEY_TRACK = "jsonTrack"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.favoritesTrackListView.adapter = favoritesTrackListAdapter
        viewModel.updateFavorites()
        //слушатель клика на трек в списке избранного
        favoritesTrackListAdapter.setOnItemClickListener { position ->
            if (clickDebounce()) {
                viewModel.showPlayer(favoritesTrackListAdapter.getTrack(position))
            }
        }
        viewModel.observeState().observe(viewLifecycleOwner, ::render)
        viewModel.observeStartPlayerEvent().observe(viewLifecycleOwner, ::showPlayer)
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateFavorites()

    }
    private fun render(state: FavoritesState) {
        when (state) {
            is FavoritesState.Empty -> {
                binding.placeholder.visibility = View.VISIBLE
                binding.favoritesTrackListView.visibility = View.GONE
            }

            is FavoritesState.Content -> {
                showContent(state.favoritesTrackList)
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
    private fun showContent(favoritesTrackList: List<Track>) {
        binding.placeholder.visibility = View.GONE
        binding.favoritesTrackListView.visibility = View.VISIBLE
        favoritesTrackListAdapter.trackList.clear()
        favoritesTrackListAdapter.trackList.addAll(favoritesTrackList)
        favoritesTrackListAdapter.notifyDataSetChanged()
    }
    private fun showPlayer(track: Track) {
        val bundle = Bundle()
        bundle.putString(KEY_TRACK, Gson().toJson(track))
        findNavController().navigate(R.id.action_LibraryFragment_to_playerFragment, bundle)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        isClickAllowed = true
    }

}