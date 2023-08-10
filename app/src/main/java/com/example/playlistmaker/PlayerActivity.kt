package com.example.playlistmaker

import android.media.MediaPlayer
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
    private var mediaPlayer = MediaPlayer()
    private var playerState = PlayerState.DEFAULT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeBackButton()
        track = Gson().fromJson(intent.getStringExtra(KEY_TRACK), Track::class.java)
        initializePlayPauseButton()
        initializeAddToFavoriteButton()
        initializeAddToPlaylistButton()
        initializeTrack(track)
        preparePlayer()
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

    private fun initializeTrack(track: Track) {
        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.trackTime.text = DateTimeUtil.ZERO
        binding.listDurationValue.text =
            SimpleDateFormat(DateTimeUtil.FORMAT_MINUTES_SECONDS, Locale.getDefault()).format(track.trackTimeMillis.toInt())
        binding.listCollectionValue.text = track.collectionName
        binding.listYearValue.text = track.releaseDate.substring(FIRST_SYMBOL, FOURTH_SYMBOL)
        binding.listGenreValue.text = track.primaryGenreName
        binding.listCountryValue.text = track.country
        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.cover_placeholder)
            .fitCenter()
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.corner_radius_small)))
            .into(binding.trackCover)
    }

    private fun initializePlayPauseButton() {
        binding.playPauseButton.setOnClickListener {
            playBackControl()
        }
    }

    private fun playBackControl() {
        when (playerState) {
            PlayerState.PREPARED, PlayerState.PAUSED -> {
                startPlayer()
            }
            PlayerState.PLAYING -> {
                pausePlayer()
            }
            PlayerState.DEFAULT -> {
                preparePlayer()
            }
        }
    }

    private fun initializeAddToFavoriteButton() {
        binding.addToFavoriteButton.setOnClickListener {
            binding.addToFavoriteButton.setImageResource(R.drawable.add_to_favorite_button_true)
        }
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            binding.playPauseButton.isEnabled = true
            playerState = PlayerState.PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            binding.playPauseButton.setImageResource(R.drawable.play_button)
            playerState = PlayerState.PREPARED
            DateTimeUtil.timerStop(binding.trackTime)
        }
    }

    private fun startPlayer() {
        binding.playPauseButton.setImageResource(R.drawable.pause_button)
        mediaPlayer.start()
        DateTimeUtil.timerStart(mediaPlayer, binding.trackTime)
        playerState = PlayerState.PLAYING
    }

    private fun pausePlayer() {
        binding.playPauseButton.setImageResource(R.drawable.play_button)
        mediaPlayer.pause()
        DateTimeUtil.timerPause()
        playerState = PlayerState.PAUSED
    }

    private fun initializeAddToPlaylistButton() {
        binding.addToPlayListButton.setOnClickListener {
            binding.addToPlayListButton.setImageResource(R.drawable.add_to_playlist_button_true)
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    companion object {
        private const val KEY_TRACK = "track"

        private const val FIRST_SYMBOL = 0
        private const val FOURTH_SYMBOL = 4
    }
}