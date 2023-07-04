package com.example.playlistmaker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    private lateinit var inputSearchText: EditText
    private lateinit var backButton: ImageView
    private lateinit var clearIcon: ImageView
    private lateinit var placeholder: LinearLayout
    private lateinit var trackList: ArrayList<Track>
    private lateinit var trackListView: RecyclerView
    private lateinit var trackListAdapter: TrackListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initializeSearchEditText(savedInstanceState)
        initializeBackButton()
        initializeClearIcon()
        initializeTrackListView()
        showPlaceholder(Placeholder.ENTER_QUERY)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val searchText = inputSearchText.text.toString()
        outState.putString(SEARCH_TEXT_KEY, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val searchText = savedInstanceState.getString(SEARCH_TEXT_KEY)
        inputSearchText.setText(searchText)
    }

    private fun initializeSearchEditText(savedInstanceState: Bundle?) {
        inputSearchText = findViewById(R.id.inputSearchText)
        if (savedInstanceState != null) {
            val searchText = savedInstanceState.getString(SEARCH_TEXT_KEY)
            inputSearchText.setText(searchText)
            hidePlaceholder()
        }
    }

    private fun initializeBackButton() {
        //Back Button
        backButton = findViewById(R.id.back_arrow)
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun initializeClearIcon() {
        clearIcon = findViewById<ImageView>(R.id.clearIcon)
        clearIcon.setOnClickListener {
            inputSearchText.text.clear()
            trackList.clear()
            trackListAdapter.notifyDataSetChanged()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(inputSearchText.windowToken, 0)
            clearIcon.visibility = View.GONE
            showPlaceholder(Placeholder.ENTER_QUERY)
        }
        // Show the clear icon when the text changes
        inputSearchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) {
                    clearIcon.visibility = View.VISIBLE
                    hidePlaceholder()
                } else {
                    clearIcon.visibility = View.GONE
                    showPlaceholder(Placeholder.EMPTY)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun initializeTrackListView() {
        trackList = ArrayList<Track>()
        trackListView = findViewById(R.id.trackList)
        trackListAdapter = TrackListAdapter(trackList)
        trackListView.adapter = trackListAdapter
        inputSearchText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                sendQuery()
                true
            }
            false
        }
    }
    private fun sendQuery(){
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.itunes_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val iTunesService = retrofit.create(ITunesSearchAPI::class.java)
        if (inputSearchText.text.toString().isNotEmpty()) {
            iTunesService.search(inputSearchText.text.toString())
                .enqueue(object : Callback<TrackResponse> {
                    override fun onResponse(
                        call: Call<TrackResponse>,
                        response: Response<TrackResponse>
                    ) {
                        trackList.clear()
                        trackListAdapter.notifyDataSetChanged()
                        if (response.code() == 200) {
                            if (response.body()?.results?.isNotEmpty() == true) {
                                trackList.addAll(response.body()?.results!!)
                                trackListAdapter.notifyDataSetChanged()
                            }
                            if (trackList.isEmpty()) {
                                showPlaceholder(Placeholder.NOTHING_FOUND)
                            } else {
                                hidePlaceholder()
                            }
                        } else {
                            showPlaceholder(Placeholder.ERROR)
                        }
                    }
                    override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                        showPlaceholder(Placeholder.ERROR, t.message.toString())
                    }
                })
        }
    }
    private fun showPlaceholder(placeHolderType: Placeholder, errorMessage: String = "") {
        placeholder = findViewById<LinearLayout>(R.id.error_placeholder)
        val placeholderImage = findViewById<ImageView>(R.id.error_image_placeholder)
        val placeholderMessage = findViewById<TextView>(R.id.error_text_placeholder)
        val placeholderButton = findViewById<Button>(R.id.reload_button)
        when (placeHolderType) {
            Placeholder.EMPTY -> {
                placeholder.visibility = View.VISIBLE
                placeholderImage.visibility = View.GONE
                placeholderMessage.visibility = View.GONE
                placeholderButton.visibility = View.GONE
            }

            Placeholder.NOTHING_FOUND -> {
                placeholder.visibility = View.VISIBLE
                placeholderImage.visibility = View.VISIBLE
                placeholderImage.setImageResource(R.drawable.nothing_found_placeholder)
                placeholderMessage.visibility = View.VISIBLE
                placeholderMessage.text = getString(R.string.nothing_found)
                placeholderButton.visibility = View.GONE
            }

            Placeholder.ERROR -> {
                placeholder.visibility = View.VISIBLE
                placeholderImage.visibility = View.VISIBLE
                placeholderImage.setImageResource(R.drawable.error_placeholder)
                placeholderMessage.visibility = View.VISIBLE
                placeholderMessage.text = getString(R.string.error)
                placeholderButton.visibility = View.VISIBLE
                placeholderButton.setOnClickListener{
                    sendQuery()
                }
            }

            Placeholder.ENTER_QUERY -> {
                placeholder.visibility = View.VISIBLE
                placeholderImage.visibility = View.GONE
                placeholderMessage.visibility = View.VISIBLE
                placeholderMessage.text = getString(R.string.enter_query)
                placeholderButton.visibility = View.GONE
            }
        }
        if (errorMessage.isNotEmpty()) {
            Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun hidePlaceholder() {
        placeholder.visibility = View.GONE
    }
    companion object {
        private const val SEARCH_TEXT_KEY = "searchText"
    }

}