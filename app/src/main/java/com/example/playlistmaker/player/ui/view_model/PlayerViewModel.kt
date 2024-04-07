package com.example.playlistmaker.player.ui.view_model

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.db.FavoritesInteractor
import com.example.playlistmaker.library.domain.db.PlaylistInteractor
import com.example.playlistmaker.player.domain.models.Track
import com.example.playlistmaker.main.domain.models.DateTimeUtil
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val track: Track,
    private val favoritesInteractor: FavoritesInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {
    private val mediaPlayer = MediaPlayer()
    private var timerJob: Job? = null
    private val playerStateLiveData = MutableLiveData<PlayerState>(PlayerState.Default())
    private val bottomSheetLiveData = MutableLiveData<BottomSheetState>(BottomSheetState.Default)

    fun observeState(): LiveData<PlayerState> = playerStateLiveData
    fun observeBottomSheetState(): LiveData<BottomSheetState> = bottomSheetLiveData

    init {
        initMediaPlayer()
        initBottomSheet()
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
        mediaPlayer.reset()
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

    private fun initBottomSheet() {
        bottomSheetLiveData.postValue(BottomSheetState.Default)
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

    fun setTrackFavoriteState(track: Track) {
        viewModelScope.launch {
            track.isFavorite = favoritesInteractor.isFavorite(track)
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

    fun onPlaylistClicked(track: Track) {
        viewModelScope.launch {
            playlistInteractor
                .getPlaylists()
                .collect { playlists ->
                    bottomSheetLiveData.postValue(
                        BottomSheetState.PlaylistsEditing(
                            playlists,
                            track
                        )
                    )
                }
        }
    }

    fun addTrackToPlaylist(position: Int, track: Track) {
        viewModelScope.launch {
            playlistInteractor.getPlaylists().collect {
                val trackAdded = playlistInteractor.insertTrack(it[position].id, track)
                bottomSheetLiveData.postValue(BottomSheetState.PlaylistEditingComplete(trackAdded, it[position].name))
            }
        }
    }
}