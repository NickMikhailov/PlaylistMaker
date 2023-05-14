package com.example.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val searchButton = findViewById<Button>(R.id.search_button)
        val displayIntent = Intent(this, SearchActivity::class.java)
        val searchClickListener: View.OnClickListener = object : View.OnClickListener
        {
            override fun onClick(v: View?) {
                startActivity(displayIntent)
                //Toast.makeText(this@MainActivity, "Нажали на поиск!", Toast.LENGTH_SHORT).show()
            }
        }
        searchButton.setOnClickListener(searchClickListener)

        val libraryButton = findViewById<Button>(R.id.library_button)
        libraryButton.setOnClickListener {
            val displayIntent = Intent(this, LibraryActivity::class.java)
            startActivity(displayIntent)
            //Toast.makeText(this@MainActivity, "Нажали на медиатеку!", Toast.LENGTH_SHORT).show()
        }
        val settingsButton = findViewById<Button>(R.id.settings_button)
        settingsButton.setOnClickListener {
            val displayIntent = Intent(this, SettingsActivity::class.java)
            startActivity(displayIntent)
        //Toast.makeText(this@MainActivity, "Нажали на настройки!", Toast.LENGTH_SHORT).show()
        }
    }
}
