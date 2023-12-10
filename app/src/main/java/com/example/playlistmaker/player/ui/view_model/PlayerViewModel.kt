package com.example.playlistmaker.player.ui.view_model

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.player.domain.models.Track
import com.example.playlistmaker.search.domain.models.DateTimeUtil

class PlayerViewModel(private val track: Track): ViewModel() {
    private val mediaPlayer = MediaPlayer()
    private val handler = Handler(Looper.getMainLooper())
    private val playerStateLiveData = MutableLiveData<PlayerState>(PlayerState.Default)

    fun observeState(): LiveData<PlayerState> = playerStateLiveData
    private fun renderState(state: PlayerState) {
        this.playerStateLiveData.postValue(state)
    }
    fun playBackControl() {
        if(mediaPlayer.isPlaying) {
            pausePlayer()
        } else {
            startPlayer()
        }
    }
    fun preparePlayer() {
            renderState(PlayerState.Default)
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            renderState(PlayerState.Prepared)
        }
        mediaPlayer.setOnCompletionListener {
            renderState(PlayerState.Stopped)
        }
    }
    private fun startPlayer(){
        mediaPlayer.start()
        timerStart()
        playerStateLiveData.value = PlayerState.Playing
    }
    fun pausePlayer(){
        mediaPlayer.pause()
        playerStateLiveData.value = PlayerState.Paused
    }
    private fun timerStart() {
        handler.post(
            updateTimer()
        )
    }
    private fun updateTimer(): Runnable {
        return object : Runnable {
            override fun run() {
                val currentPosition = mediaPlayer.currentPosition/ DateTimeUtil.MILLISECONDS_IN_A_SECOND
                if (mediaPlayer.isPlaying) {
                    val currentTime  = String.format("%02d:%02d", currentPosition / DateTimeUtil.SECONDS_IN_A_MINUTE, currentPosition % DateTimeUtil.SECONDS_IN_A_MINUTE)
                    renderState(PlayerState.TimerUpdated(currentTime))
                    handler.postDelayed(this, DateTimeUtil.QUARTER_SECOND_DELAY)
                }
            }
        }
    }
    public override fun onCleared() {
        handler.removeCallbacksAndMessages(null)
        mediaPlayer.release()
    }
    companion object{
        fun factory(track: Track): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(track)
            }
        }
    }
}