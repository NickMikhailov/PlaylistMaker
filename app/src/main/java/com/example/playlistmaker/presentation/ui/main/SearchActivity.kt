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
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.ui.track.TrackListAdapter
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.models.DateTimeUtil
import com.example.playlistmaker.domain.models.Placeholder
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.use_case.SearchHistory
import com.google.gson.Gson

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var trackList: ArrayList<Track>
    private lateinit var trackListAdapter: TrackListAdapter

    private lateinit var searchHistory: SearchHistory
    private lateinit var searchHistoryAdapter: TrackListAdapter
    private lateinit var queryInput: EditText
    private val searchRunnable = Runnable { sendRequest() }

    private lateinit var sharedPreferences: SharedPreferences

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    private val tracksInteractor = Creator.provideTracksInteractor()
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
        showPlaceholder(Placeholder.EMPTY)
    }

    override fun onResume() {
        super.onResume()
        initializeSearchHistoryView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, queryInput.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val searchText = savedInstanceState.getString(SEARCH_TEXT_KEY)
        queryInput.setText(searchText)
    }

    private fun initializeSearchField(savedInstanceState: Bundle?) {
        queryInput = binding.searchField
        if (savedInstanceState != null) {
            val searchText = savedInstanceState.getString(SEARCH_TEXT_KEY)
            queryInput.setText(searchText)
            hidePlaceholder()
        }
        queryInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) {
                    binding.clearIcon.visibility = View.VISIBLE
                    hidePlaceholder()
                    searchDebounce()
                } else {
                    binding.clearIcon.visibility = View.GONE
                    showPlaceholder(Placeholder.HISTORY)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        queryInput.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && queryInput.text.isEmpty() && searchHistory.getHistory().size != 0) {
                showPlaceholder(Placeholder.HISTORY)
            } else {
                showPlaceholder(Placeholder.EMPTY)
            }
        }
        queryInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                sendRequest()
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
            queryInput.text.clear()
            trackList.clear()
            trackListAdapter.notifyDataSetChanged()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(queryInput.windowToken, 0)
            binding.clearIcon.visibility = View.GONE
            showPlaceholder(Placeholder.HISTORY)
        }
    }

    private fun showPlayer(track: Track) {
        val displayIntent = Intent(this, PlayerActivity::class.java)
        searchHistory.addToHistory(track)
        displayIntent.putExtra(KEY_TRACK, Gson().toJson(track))
        startActivity(displayIntent)
    }

    private fun sendRequest() {
//        val useCase = getTrackListUseCase(tracksRepository,queryInput.text)
        showPlaceholder(Placeholder.PROGRESSBAR)
        if (queryInput.text.isNotEmpty()) {
            tracksInteractor.search(queryInput.text.toString(), object: TracksInteractor.TracksConsumer {
                override fun consume(foundTrack: List<Track>) {
                    handler.post {
                        trackList.clear()
                        trackList.addAll(foundTrack)
                        trackListAdapter.notifyDataSetChanged()
                    }
                }
            })
            if (trackList.isEmpty()) {
                showPlaceholder(Placeholder.NOTHING_FOUND)
            } else {
                hidePlaceholder()
            }
        } else {
            showPlaceholder(Placeholder.ERROR)
        }
//        trackListAdapter.notifyDataSetChanged()
    }

    private fun showPlaceholder(placeHolderType: Placeholder, errorMessage: String = "") {
        binding.placeholder.visibility = View.VISIBLE
        binding.placeholderImage.visibility = View.GONE
        binding.placeholderText.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.searchHistoryView.visibility = View.GONE
        binding.placeholderButton.visibility = View.GONE
        binding.placeholder.gravity = Gravity.CENTER_VERTICAL

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
                    sendRequest()
                }
            }

            Placeholder.HISTORY -> {
                binding.placeholder.gravity = Gravity.TOP
                binding.placeholderText.visibility = View.VISIBLE
                binding.placeholderText.text = getString(R.string.search_history)
                binding.searchHistoryView.visibility = View.VISIBLE
                binding.placeholderButton.visibility = View.VISIBLE
                binding.placeholderButton.text = getString(R.string.clear_history)
                binding.placeholderButton.setOnClickListener {
                    searchHistory.clearHistory()
                    showPlaceholder(Placeholder.EMPTY)
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
    private fun hidePlaceholder() {
        binding.placeholder.visibility = View.GONE
    }
    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, DateTimeUtil.CLICK_DEBOUNCE_DELAY)
        }
        return current
    }
    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, DateTimeUtil.SEARCH_DEBOUNCE_DELAY)
    }
    companion object {
        private const val SEARCH_TEXT_KEY = "searchText"
        private const val KEY_TRACK = "track"
        private const val TRACK_HISTORY = "track_history"
    }
}