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
    private lateinit var track: Track
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeBackButton()
        track = Gson().fromJson(intent.getStringExtra(KEY_TRACK), Track::class.java)
        initializeTrack(track)
        initializePlayPauseButton()
        initializeAddToFavoriteButton()
        initializeAddToPlaylistButton()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_TRACK, Gson().toJson(track))
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        track = Gson().fromJson(savedInstanceState.getString(KEY_TRACK), Track::class.java)
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
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.corner_radius_small)))
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
    companion object {
        private const val KEY_TRACK = "track"
    }
}