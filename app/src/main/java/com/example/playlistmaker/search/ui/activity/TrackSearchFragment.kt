package com.example.playlistmaker.search.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.player.domain.models.Track
import com.example.playlistmaker.player.ui.activity.PlayerActivity
import com.example.playlistmaker.search.domain.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.models.DateTimeUtil
import com.example.playlistmaker.search.domain.models.Placeholder
import com.example.playlistmaker.search.ui.TrackListAdapter
import com.example.playlistmaker.search.ui.view_model.TrackSearchState
import com.example.playlistmaker.search.ui.view_model.TrackSearchViewModel
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class TrackSearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private var trackListAdapter = TrackListAdapter(ArrayList())
    private var searchHistoryAdapter = TrackListAdapter(ArrayList())
    private var isClickAllowed = true
    private val viewModel by viewModel<TrackSearchViewModel>()

    companion object {
        private const val KEY_TRACK = "track"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.trackListView.adapter = trackListAdapter
        binding.searchHistoryView.adapter = searchHistoryAdapter

        setListeners()

        viewModel.observeState().observe(viewLifecycleOwner) { render(it) }
        viewModel.observeStartPlayerEvent()
            .observe(viewLifecycleOwner) { track -> showPlayer(track) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setListeners() {
        //слушатели поля поиска
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) {
                    binding.clearIcon.visibility = View.VISIBLE
                    viewModel.searchDebounce(changedText = s?.toString() ?: "")
                } else {
                    binding.clearIcon.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        textWatcher.let { binding.searchField.addTextChangedListener(it) }
        binding.searchField.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && binding.searchField.text.isEmpty()) {
                viewModel.showHistory()
            }
        }
        binding.searchField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.sendRequest(newSearchText = binding.searchField.text.toString())
            }
            false
        }

        //слушатель кнопки очистки строки поиска
        binding.clearIcon.setOnClickListener {
            binding.searchField.text.clear()
            val imm =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.searchField.windowToken, 0)
            binding.clearIcon.visibility = View.GONE
            viewModel.showHistory()
        }
        //слушатель клика на трек в списке поиска
        trackListAdapter.setOnItemClickListener(object : TrackListAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                if (clickDebounce()) {
                    viewModel.showPlayer(trackListAdapter.getTrack(position))
                }
            }
        })
        //слушатель клика на трек в списке истории поиска
        searchHistoryAdapter.setOnItemClickListener(object : TrackListAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                viewModel.showPlayer(searchHistoryAdapter.getTrack(position))
            }
        })
        //слушатель кнопки очистки истории
        binding.clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
        }
        //слушатель кнопки "обновить" плейсхолдера
        binding.placeholderButton.setOnClickListener {
            viewModel.sendRequest(newSearchText = binding.searchField.text.toString())
        }
    }

    private fun showPlayer(track: Track) {
        val displayIntent = Intent(requireContext(), PlayerActivity::class.java)
        displayIntent.putExtra(KEY_TRACK, Gson().toJson(track))
        startActivity(displayIntent)
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

    private fun render(state: TrackSearchState) {
        when (state) {
            is TrackSearchState.Loading -> {
                showPlaceholder(Placeholder.PROGRESSBAR)
            }

            is TrackSearchState.Content -> {
                showContent(state.trackList)
            }

            is TrackSearchState.Error -> {
                showPlaceholder(state.placeholder, state.errorMessage)
            }

            is TrackSearchState.History -> {
                showHistory(state.searchHistory)
            }
        }
    }

    private fun showContent(newTrackList: List<Track>) {
        binding.placeholder.visibility = View.GONE
        binding.history.visibility = View.GONE
        trackListAdapter.trackList.clear()
        trackListAdapter.trackList.addAll(newTrackList)
        trackListAdapter.notifyDataSetChanged()
    }

    private fun showHistory(searchHistory: SearchHistoryInteractor) {
        searchHistoryAdapter.update(searchHistory.getHistory())
        searchHistoryAdapter.notifyDataSetChanged()
        binding.placeholder.visibility = View.GONE
        binding.history.visibility = View.VISIBLE
    }

    private fun showPlaceholder(placeHolderType: Placeholder, errorMessage: Int = 0) {
        binding.placeholder.visibility = View.VISIBLE
        binding.history.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.placeholderImage.visibility = View.GONE
        binding.placeholderText.visibility = View.GONE
        binding.placeholderButton.visibility = View.GONE

        when (placeHolderType) {
            Placeholder.EMPTY -> {
            }

            Placeholder.NOTHING_FOUND -> {
                binding.placeholderImage.visibility = View.VISIBLE
                binding.placeholderImage.setImageResource(R.drawable.nothing_found_placeholder)
                binding.placeholderText.visibility = View.VISIBLE
                binding.placeholderText.text = getString(R.string.nothing_found)
            }

            Placeholder.ERROR -> {
                binding.placeholderImage.visibility = View.VISIBLE
                binding.placeholderImage.setImageResource(R.drawable.error_placeholder)
                binding.placeholderText.visibility = View.VISIBLE
                binding.placeholderText.text = getString(R.string.error)
                binding.placeholderButton.visibility = View.VISIBLE
                binding.placeholderButton.text = getString(R.string.update)

            }

            Placeholder.PROGRESSBAR -> {
                binding.progressBar.visibility = View.VISIBLE
            }
        }
        if (errorMessage != 0) {
            Toast.makeText(requireContext(), getString(errorMessage), Toast.LENGTH_LONG)
                .show()
        }
    }
}