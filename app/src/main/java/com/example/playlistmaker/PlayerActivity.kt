package com.example.playlistmaker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeBackButton()
        val track = Gson().fromJson(intent.getStringExtra("track"), Track::class.java)
        initializeTrack(track)
        initializePlayPauseButton()
        initializeAddToFavoriteButton()
        initializeAddToPlaylistButton()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val track = intent.getStringExtra("track")
        outState.putString("track", track)
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val track = Gson().fromJson(savedInstanceState.getString("track"), Track::class.java)
        initializeTrack(track)
    }
    private fun initializeBackButton() {
        binding.arrowBack.setOnClickListener {
            finish()
        }
    }
    private fun initializeTrack(track:Track) {
        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.trackTime.text = "00:00"
        binding.listDurationValue.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis.toInt())
        binding.listCollectionValue.text = track.collectionName
        binding.listYearValue.text = track.releaseDate.substring(0,4)
        binding.listGenreValue.text = track.primaryGenreName
        binding.listCountryValue.text = track.country
        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.cover_placeholder)
            .fitCenter()
            .transform(RoundedCorners(16)) //не получается подтянуть 8 из R.dimen.corner_radius_large
            .into(binding.trackCover)    }

    private fun initializePlayPauseButton() {
        binding.playPauseButton.setOnClickListener{
            binding.playPauseButton.setImageResource(R.drawable.pause_button)
        }
    }
    private fun initializeAddToFavoriteButton() {
        binding.addToFavoriteButton.setOnClickListener{
            binding.addToFavoriteButton.setImageResource(R.drawable.add_to_favorite_button_true)
        }
    }    private fun initializeAddToPlaylistButton() {
        binding.addToPlayListButton.setOnClickListener{
            binding.addToPlayListButton.setImageResource(R.drawable.add_to_playlist_button_true)
        }
    }
}