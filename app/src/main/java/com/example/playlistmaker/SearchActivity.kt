package com.example.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView

class SearchActivity : AppCompatActivity() {
    private var searchText: String? = null
    private lateinit var inputSearchText: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        inputSearchText = findViewById<EditText>(R.id.inputSearchText)
        val clearIcon = findViewById<ImageView>(R.id.clearIcon)

    // Иконка очистки сроки поиска
        clearIcon.setOnClickListener {
            inputSearchText.text.clear()
            clearIcon.visibility = View.GONE
        }

        // Show the clear icon when the text changes
        inputSearchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) {
                    clearIcon.visibility = View.VISIBLE
                } else {
                    clearIcon.visibility = View.GONE
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString("searchText")
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val searchText = inputSearchText.text.toString()
        outState.putString("searchText", searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString("searchText")
    }
}