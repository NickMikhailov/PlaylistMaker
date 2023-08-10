package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var trackList: ArrayList<Track>
    private lateinit var trackListAdapter: TrackListAdapter

    private lateinit var searchHistory: SearchHistory
    private lateinit var searchHistoryAdapter: TrackListAdapter

    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("track_history", Context.MODE_PRIVATE)

        initializeSearchField(savedInstanceState)
        initializeBackButton()
        initializeTrackListView()
        initializeClearIcon()
        showPlaceholder(Placeholder.ENTER_QUERY)
    }

    override fun onResume() {
        super.onResume()
        initializeSearchHistoryView()
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val searchText = binding.searchField.text.toString()
        outState.putString(SEARCH_TEXT_KEY, searchText)
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
            hidePlaceholder()
        }
        binding.searchField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) {
                    binding.clearIcon.visibility = View.VISIBLE
                    hidePlaceholder()
                } else {
                    binding.clearIcon.visibility = View.GONE
                    showPlaceholder(Placeholder.EMPTY)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        binding.searchField.setOnFocusChangeListener { view, hasFocus ->
            if(hasFocus && binding.searchField.text.isEmpty() && searchHistory.getHistory().size!=0) {
                showPlaceholder(Placeholder.HISTORY)
            } else {
                showPlaceholder(Placeholder.ENTER_QUERY)
            }
        }
        binding.searchField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                sendQuery()
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
                val track = trackListAdapter.getTrack(position)
                showPlayer(track)
            }
        })
    }
    private fun initializeSearchHistoryView(){
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
            initializeSearchHistoryView()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.searchField.windowToken, 0)
            binding.clearIcon.visibility = View.GONE
            showPlaceholder(Placeholder.HISTORY)
        }
    }
    private fun showPlayer(track: Track){
        val displayIntent = Intent(this, PlayerActivity::class.java)
        searchHistory.addToHistory(track)
        displayIntent.putExtra(KEY_TRACK, Gson().toJson(track))
        startActivity(displayIntent)
    }
    private fun sendQuery() {
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.itunes_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val iTunesService = retrofit.create(ITunesSearchAPI::class.java)
        if (binding.searchField.text.toString().isNotEmpty()) {
            iTunesService.search(binding.searchField.text.toString())
                .enqueue(object : Callback<TrackResponse> {
                    override fun onResponse(
                        call: Call<TrackResponse>,
                        response: Response<TrackResponse>
                    ) {
                        trackList.clear()
                        if (response.code() == 200) {
                            if (response.body()?.results?.isNotEmpty() == true) {
                                trackList.addAll(response.body()?.results!!)
                            }
                            if (trackList.isEmpty()) {
                                showPlaceholder(Placeholder.NOTHING_FOUND)
                            } else {
                                hidePlaceholder()
                            }
                        } else {
                            showPlaceholder(Placeholder.ERROR)
                        }
                        trackListAdapter.notifyDataSetChanged()
                    }

                    override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                        showPlaceholder(Placeholder.ERROR, t.message.toString())
                    }
                })
        }
    }
    private fun showPlaceholder(placeHolderType: Placeholder, errorMessage: String = "") {
        binding.placeholder.visibility = View.VISIBLE
        binding.placeholder.gravity = Gravity.CENTER_VERTICAL
        when (placeHolderType) {
            Placeholder.EMPTY -> {
                binding.placeholderImage.visibility = View.GONE
                binding.placeholderText.visibility = View.GONE
                binding.searchHistoryView.visibility = View.GONE
                binding.placeholderButton.visibility = View.GONE
            }
            Placeholder.NOTHING_FOUND -> {
                binding.placeholderImage.visibility = View.VISIBLE
                binding.placeholderImage.setImageResource(R.drawable.nothing_found_placeholder)
                binding.placeholderText.visibility = View.VISIBLE
                binding.placeholderText.text = getString(R.string.nothing_found)
                binding.searchHistoryView.visibility = View.GONE
                binding.placeholderButton.visibility = View.GONE
            }
            Placeholder.ERROR -> {
                binding.placeholderImage.visibility = View.VISIBLE
                binding.placeholderImage.setImageResource(R.drawable.error_placeholder)
                binding.placeholderText.visibility = View.VISIBLE
                binding.placeholderText.text = getString(R.string.error)
                binding.searchHistoryView.visibility = View.GONE
                binding.placeholderButton.visibility = View.VISIBLE
                binding.placeholderButton.text = getString(R.string.update)
                binding.placeholderButton.setOnClickListener {
                    sendQuery()
                }
            }
            Placeholder.ENTER_QUERY -> {
                binding.placeholderImage.visibility = View.GONE
                binding.placeholderText.visibility = View.VISIBLE
                binding.placeholderText.text = getString(R.string.enter_query)
                binding.searchHistoryView.visibility = View.GONE
                binding.placeholderButton.visibility = View.GONE
            }
            Placeholder.HISTORY ->{
                binding.placeholder.gravity = Gravity.TOP
                binding.placeholderImage.visibility = View.GONE
                binding.placeholderText.visibility = View.VISIBLE
                binding.placeholderText.text = getString(R.string.search_history)
                binding.searchHistoryView.visibility = View.VISIBLE
                binding.placeholderButton.visibility = View.VISIBLE
                binding.placeholderButton.text = getString(R.string.clear_history)
                binding.placeholderButton.setOnClickListener {
                    searchHistory.clearHistory()
                    showPlaceholder(Placeholder.ENTER_QUERY)
                }
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
    companion object {
        private const val SEARCH_TEXT_KEY = "searchText"
        private const val KEY_TRACK = "track"
    }
}