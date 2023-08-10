package com.example.playlistmaker

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.widget.TextView

object DateTimeUtil {
    const val ZERO = "00:00"
    const val FORMAT_MINUTES_SECONDS = "mm:ss"
    const val CLICK_DEBOUNCE_DELAY = 1000L
    const val SEARCH_DEBOUNCE_DELAY = 2000L
    const val QUARTER_SECOND_DELAY = 250L
    const val MILLISECONDS_IN_A_SECOND = 1000L
    const val SECONDS_IN_A_MINUTE = 60L
    var isTimerOn = false

    private var mainThreadHandler: Handler? = Handler(Looper.getMainLooper())

    fun timerStart(mediaPlayer: MediaPlayer, textView: TextView) {
        isTimerOn = true
        mainThreadHandler?.post(
            createUpdateTimerTask(mediaPlayer, textView)
        )
    }
    fun timerStop(textView: TextView) {
        isTimerOn = false
        textView.text = ZERO
    }

    fun timerPause(){
        isTimerOn = false
    }

    private fun createUpdateTimerTask(mediaPlayer: MediaPlayer, textView: TextView): Runnable {
        return object : Runnable {
            override fun run() {
                val currentPosition = mediaPlayer.currentPosition/MILLISECONDS_IN_A_SECOND
                if (isTimerOn) {
                    textView.text = String.format("%d:%02d", currentPosition / SECONDS_IN_A_MINUTE, currentPosition % SECONDS_IN_A_MINUTE)
                    mainThreadHandler?.postDelayed(this, QUARTER_SECOND_DELAY)
                }
            }
        }
    }
}