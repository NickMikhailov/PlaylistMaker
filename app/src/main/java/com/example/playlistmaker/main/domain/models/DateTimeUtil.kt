package com.example.playlistmaker.main.domain.models

import java.text.SimpleDateFormat
import java.util.Locale

object DateTimeUtil {
    const val ZERO = "00:00"
    const val FORMAT_MINUTES_SECONDS = "mm:ss"
    const val CLICK_DEBOUNCE_DELAY = 1000L
    const val SEARCH_DEBOUNCE_DELAY = 2000L
    const val MS_300_DELAY = 300L
    const val MM = "mm"
    fun convertMillisToMinutes(durationInMillis: Long): String {
        return "${
            SimpleDateFormat(MM, Locale.getDefault())
                .format(durationInMillis)
        } ${
            GrammarUtil.getEnding(durationInMillis / 60000L, "минута", "минуты", "минут")
        }"
    }
}