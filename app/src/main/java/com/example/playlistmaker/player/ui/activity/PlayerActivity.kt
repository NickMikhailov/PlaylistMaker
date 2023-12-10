package com.example.playlistmaker.player.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.search.domain.models.DateTimeUtil
import com.example.playlistmaker.player.domain.models.Track
import com.example.playlistmaker.player.ui.view_model.PlayerState
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private lateinit var track: Track
    private val viewModel: PlayerViewModel by viewModel {
        parametersOf(track)
    }
    //    private val viewModel by lazy {
//        ViewModelProvider(this, PlayerViewModel.factory(track))[PlayerViewModel::class.java]
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        track = Gson().fromJson(intent.getStringExtra(KEY_TRACK), Track::class.java)
        viewModel.preparePlayer()
        renderTrack(track)
        setListeners()
        viewModel.observeState().observe(this) {
            render(it)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_TRACK, Gson().toJson(track))
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        track = Gson().fromJson(savedInstanceState.getString(KEY_TRACK), Track::class.java)
        renderTrack(track)
    }

    private fun setListeners() {
        binding.arrowBack.setOnClickListener {
            finish()
        }
        binding.playPauseButton.setOnClickListener {
            viewModel.playBackControl()
        }
        //заглушки для кнопок:
        binding.addToFavoriteButton.setOnClickListener {
            binding.addToFavoriteButton.setImageResource(R.drawable.add_to_favorite_button_true)
        }
        binding.addToPlayListButton.setOnClickListener {
            binding.addToPlayListButton.setImageResource(R.drawable.add_to_playlist_button_true)
        }
    }

    private fun renderTrack(track: Track) {
        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.trackTime.text = DateTimeUtil.ZERO
        binding.listDurationValue.text = track.trackTime
        binding.listCollectionValue.text = track.collectionName
        binding.listYearValue.text = track.releaseDate.substring(FIRST_SYMBOL, FOURTH_SYMBOL)
        binding.listGenreValue.text = track.primaryGenreName
        binding.listCountryValue.text = track.country
        Glide.with(this)
            .load(track.artworkUrl500)
            .placeholder(R.drawable.cover_placeholder)
            .fitCenter()
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.corner_radius_medium)))
            .into(binding.trackCover)
    }

    private fun render(state: PlayerState) {
        when (state) {
            is PlayerState.Default -> {
                binding.playPauseButton.isEnabled = false
            }

            is PlayerState.Prepared -> {
                binding.playPauseButton.isEnabled = true
            }

            is PlayerState.Paused -> {
                binding.playPauseButton.setImageResource(R.drawable.play_button)
            }

            is PlayerState.Playing -> {
                binding.playPauseButton.setImageResource(R.drawable.pause_button)
            }

            is PlayerState.Stopped -> {
                binding.playPauseButton.setImageResource(R.drawable.play_button)
                binding.trackTime.text = DateTimeUtil.ZERO
            }

            is PlayerState.TimerUpdated -> {
                binding.trackTime.text = state.currentTime
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onCleared()
    }

    companion object {
        private const val KEY_TRACK = "track"
        private const val FIRST_SYMBOL = 0
        private const val FOURTH_SYMBOL = 4
    }
}