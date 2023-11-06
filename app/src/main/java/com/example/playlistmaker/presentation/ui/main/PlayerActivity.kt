package com.example.playlistmaker.presentation.ui.main

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.domain.models.DateTimeUtil
import com.example.playlistmaker.domain.models.PlayerState
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private lateinit var track: Track
    private var mediaPlayer = MediaPlayer()
    private var playerState = PlayerState.DEFAULT
    var isTimerOn = false
    private var mainThreadHandler: Handler? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mainThreadHandler =  Handler(Looper.getMainLooper())

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
        binding.listDurationValue.text = track.trackTime
//            SimpleDateFormat(DateTimeUtil.FORMAT_MINUTES_SECONDS, Locale.getDefault()).format(track.trackTimeMillis.toInt())
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
            timerStop()
        }
    }

    private fun startPlayer() {
        binding.playPauseButton.setImageResource(R.drawable.pause_button)
        mediaPlayer.start()
        timerStart()
        playerState = PlayerState.PLAYING
    }

    private fun pausePlayer() {
        binding.playPauseButton.setImageResource(R.drawable.play_button)
        mediaPlayer.pause()
        timerPause()
        playerState = PlayerState.PAUSED
    }

    private fun initializeAddToPlaylistButton() {
        binding.addToPlayListButton.setOnClickListener {
            binding.addToPlayListButton.setImageResource(R.drawable.add_to_playlist_button_true)
        }
    }
    fun timerStart() {
        isTimerOn = true
        mainThreadHandler?.post(
            createUpdateTimerTask()
        )
    }
    fun timerStop() {
        isTimerOn = false
        binding.trackTime.text = DateTimeUtil.ZERO
    }

    fun timerPause(){
        isTimerOn = false
    }

    private fun createUpdateTimerTask(): Runnable {
        return object : Runnable {
            override fun run() {
                val currentPosition = mediaPlayer.currentPosition/ DateTimeUtil.MILLISECONDS_IN_A_SECOND
                if (isTimerOn) {
                    binding.trackTime.text  = String.format("%02d:%02d", currentPosition / DateTimeUtil.SECONDS_IN_A_MINUTE, currentPosition % DateTimeUtil.SECONDS_IN_A_MINUTE)
                    mainThreadHandler?.postDelayed(this, DateTimeUtil.QUARTER_SECOND_DELAY)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainThreadHandler?.removeCallbacksAndMessages(null)
        mediaPlayer.release()
        Log.d("crushSearch","PlayerActivity is destroyed")
    }

    companion object {
        private const val KEY_TRACK = "track"

        private const val FIRST_SYMBOL = 0
        private const val FOURTH_SYMBOL = 4
    }
}