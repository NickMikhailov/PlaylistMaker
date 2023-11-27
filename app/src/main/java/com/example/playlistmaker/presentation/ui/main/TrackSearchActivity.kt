package com.example.playlistmaker.presentation.ui.main

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.ui.track.TrackListAdapter
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.domain.models.DateTimeUtil
import com.example.playlistmaker.domain.models.Placeholder
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.use_case.SearchHistory
import com.example.playlistmaker.presentation.view_model.TrackSearchState
import com.example.playlistmaker.presentation.view_model.TrackSearchViewModel
import com.google.gson.Gson

class TrackSearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var trackList: ArrayList<Track>
    private lateinit var trackListAdapter: TrackListAdapter
    private lateinit var searchHistory: SearchHistory
    private lateinit var searchHistoryAdapter: TrackListAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private val viewModel by lazy {
        ViewModelProvider(this, TrackSearchViewModel.factory())[TrackSearchViewModel::class.java]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = getSharedPreferences(TRACK_HISTORY, Context.MODE_PRIVATE)
        initializeSearchField(savedInstanceState)
        initializeSearchHistoryView()
        initializeBackButton()
        initializeTrackListView()
        initializeClearIcon()
        viewModel.observeState().observe(this) {
            render(it)
        }
//        viewModel.getState().observe(this){state -> render(state)}
//        viewModel.getStartPlayEvent().observe(this){track -> showPlayer(track)}
    }

    override fun onResume() {
        super.onResume()
        initializeSearchHistoryView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, binding.searchField.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val searchText = savedInstanceState.getString(SEARCH_TEXT_KEY)
        binding.searchField.setText(searchText)
    }

    private fun initializeSearchField(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            val searchText = savedInstanceState.getString(SEARCH_TEXT_KEY)
            binding.searchField.setText(searchText)
        }
        var textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                showHistory()
                if (s.toString().isNotEmpty()) {
                    binding.clearIcon.visibility = View.VISIBLE
                    viewModel.searchDebounce(changedText = s?.toString() ?: "")
                } else {
                    binding.clearIcon.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        textWatcher?.let { binding.searchField.addTextChangedListener(it) }
        binding.searchField.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && binding.searchField.text.isEmpty() && searchHistory.getHistory().size != 0) {
                showHistory()
            } else {
                showPlaceholder(Placeholder.EMPTY)
            }
        }
        binding.searchField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.sendRequest(newSearchText = binding.searchField.text.toString())
                true
            }
            false
        }
    }

    private fun initializeBackButton() {
        binding.arrowBack.setOnClickListener {
            finish()
        }
    }

    private fun initializeTrackListView() {
        trackList = ArrayList()
        trackListAdapter = TrackListAdapter(trackList)
        binding.trackListView.adapter = trackListAdapter
        trackListAdapter.setOnItemClickListener(object : TrackListAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                if (clickDebounce()) {
                    showPlayer(trackListAdapter.getTrack(position))
                }
            }
        })
    }

    private fun initializeSearchHistoryView() {
        searchHistory = SearchHistory(sharedPreferences)
        searchHistoryAdapter = TrackListAdapter(searchHistory.getHistory())
        binding.searchHistoryView.adapter = searchHistoryAdapter
        searchHistoryAdapter.setOnItemClickListener(object : TrackListAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val track = searchHistoryAdapter.getTrack(position)
                showPlayer(track)
            }
        })
    }

    private fun initializeClearIcon() {
        binding.clearIcon.setOnClickListener {
            binding.searchField.text.clear()
            trackList.clear()
            trackListAdapter.notifyDataSetChanged()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.searchField.windowToken, 0)
            binding.clearIcon.visibility = View.GONE
        }
    }

    private fun showPlayer(track: Track) {
        val displayIntent = Intent(this, PlayerActivity::class.java)
        searchHistory.addToHistory(track)//будет проблема
        displayIntent.putExtra(KEY_TRACK, Gson().toJson(track))
        startActivity(displayIntent)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, DateTimeUtil.CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun clearHistory() {
        searchHistory.clearHistory()
    }

    private fun render(state: TrackSearchState) {
        when(state){
            is TrackSearchState.Loading -> showPlaceholder(Placeholder.PROGRESSBAR)
            is TrackSearchState.Content -> showContent(state.trackList)
            is TrackSearchState.Error -> showPlaceholder(state.placeholder, state.errorMessage)
            is TrackSearchState.History -> showHistory()
        }
    }
    private fun showContent(newTrackList: List<Track>) {
        binding.placeholder.visibility = View.GONE
        binding.history.visibility = View.GONE
        trackListAdapter.trackList.clear()
        trackListAdapter.trackList.addAll(newTrackList)
        trackListAdapter.notifyDataSetChanged()
    }


    private fun showPlaceholder(placeHolderType: Placeholder, errorMessage: String = "") {
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
                binding.placeholderButton.setOnClickListener {
                    viewModel.sendRequest(newSearchText = binding.searchField.text.toString())
                }
            }
            Placeholder.PROGRESSBAR -> {
                binding.progressBar.visibility = View.VISIBLE
            }
        }
        if (errorMessage.isNotEmpty()) {
            Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_LONG)
                .show()
        }
    }
    fun showHistory() {
        binding.placeholder.visibility = View.GONE
        binding.history.visibility = View.VISIBLE
        binding.clearHistoryButton.setOnClickListener {
            clearHistory()
            showPlaceholder(Placeholder.EMPTY)
        }
    }
    companion object {
        private const val SEARCH_TEXT_KEY = "searchText"
        private const val KEY_TRACK = "track"
        private const val TRACK_HISTORY = "track_history"
    }
}