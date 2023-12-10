package com.example.playlistmaker.search.ui.activity

import android.content.Context
import android.content.Intent
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
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.search.domain.models.DateTimeUtil
import com.example.playlistmaker.player.domain.models.Track
import com.example.playlistmaker.player.ui.activity.PlayerActivity
import com.example.playlistmaker.search.domain.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.models.Placeholder
import com.example.playlistmaker.search.ui.TrackListAdapter
import com.example.playlistmaker.search.ui.view_model.TrackSearchState
import com.example.playlistmaker.search.ui.view_model.TrackSearchViewModel
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class TrackSearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private var trackListAdapter = TrackListAdapter(ArrayList())
    private var searchHistoryAdapter = TrackListAdapter(ArrayList())
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private val viewModel by viewModel<TrackSearchViewModel>()
//    private val viewModel by lazy {
//        ViewModelProvider(this, TrackSearchViewModel.factory())[TrackSearchViewModel::class.java]
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.trackListView.adapter = trackListAdapter
        binding.searchHistoryView.adapter = searchHistoryAdapter

        setListeners()

        viewModel.observeState().observe(this) { render(it) }
        viewModel.observeStartPlayerEvent().observe(this) { track -> showPlayer(track) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, binding.searchField.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        binding.searchField.setText(savedInstanceState.getString(SEARCH_TEXT_KEY))
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
        //слушатель кнопки назад
        binding.arrowBack.setOnClickListener {
            finish()
        }
        //слушатель кнопки очистки строки поиска
        binding.clearIcon.setOnClickListener {
            binding.searchField.text.clear()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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
        val displayIntent = Intent(this, PlayerActivity::class.java)
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
            Toast.makeText(applicationContext, getString(errorMessage), Toast.LENGTH_LONG)
                .show()
        }
    }
    companion object {
        private const val SEARCH_TEXT_KEY = "searchText"
        private const val KEY_TRACK = "track"
    }
}