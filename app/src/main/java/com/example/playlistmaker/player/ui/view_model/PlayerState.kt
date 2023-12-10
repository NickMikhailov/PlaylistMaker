package com.example.playlistmaker.player.ui.view_model

sealed interface PlayerState {
    object Default: PlayerState
    object Prepared: PlayerState
    object Paused: PlayerState
    object Playing: PlayerState
    object Stopped: PlayerState
    data class TimerUpdated(val currentTime:String): PlayerState

}