package com.example.playlistmaker.player.ui.view_model

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.db.FavoritesInteractor
import com.example.playlistmaker.player.domain.models.Track
import com.example.playlistmaker.search.domain.models.DateTimeUtil
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val track: Track,
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {
    private val mediaPlayer = MediaPlayer()
    private var timerJob: Job? = null
    private val playerStateLiveData = MutableLiveData<PlayerState>(PlayerState.Default())
    fun observeState(): LiveData<PlayerState> = playerStateLiveData

    init {
        initMediaPlayer()
    }

    fun onPause() {
        pausePlayer()
    }

    public override fun onCleared() {
        super.onCleared()
        mediaPlayer.stop()
        mediaPlayer.release()
        playerStateLiveData.value = PlayerState.Default()
    }

    private fun initMediaPlayer() {
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerStateLiveData.postValue(PlayerState.Prepared())
        }
        mediaPlayer.setOnCompletionListener {
            timerJob?.cancel()
            playerStateLiveData.postValue(PlayerState.Prepared())
        }
    }

    fun onPlayButtonClicked() {
        when (playerStateLiveData.value) {
            is PlayerState.Playing -> {
                pausePlayer()
            }

            is PlayerState.Prepared, is PlayerState.Paused -> {
                startPlayer()
            }

            else -> {}
        }
    }

    fun onFavoriteClicked(track: Track) {
        viewModelScope.launch {

            if (favoritesInteractor.isFavorite(track)) {
             favoritesInteractor.removeFromFavorites(track)
          } else {
              favoritesInteractor.addToFavorites(track)
            }
                playerStateLiveData.postValue(playerStateLiveData.value)

      }
  }

  private fun startPlayer() {
      mediaPlayer.start()
      playerStateLiveData.postValue(PlayerState.Playing(getCurrentPlayerPosition()))
      startTimer()
  }

  private fun pausePlayer() {
      mediaPlayer.pause()
      timerJob?.cancel()
      playerStateLiveData.postValue(PlayerState.Paused(getCurrentPlayerPosition()))
  }

  private fun startTimer() {
      timerJob = viewModelScope.launch {
          while (mediaPlayer.isPlaying) {
              delay(DateTimeUtil.MS_300_DELAY)
              playerStateLiveData.postValue(PlayerState.Playing(getCurrentPlayerPosition()))
          }
      }
  }

  private fun getCurrentPlayerPosition(): String {
      return SimpleDateFormat(DateTimeUtil.FORMAT_MINUTES_SECONDS, Locale.getDefault()).format(
          mediaPlayer.currentPosition
      ) ?: DateTimeUtil.ZERO
  }
}