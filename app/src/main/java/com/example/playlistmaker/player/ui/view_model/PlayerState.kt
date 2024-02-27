package com.example.playlistmaker.player.ui.view_model

import com.example.playlistmaker.search.domain.models.DateTimeUtil

sealed class PlayerState(val progress: String) {
    class Default : PlayerState(DateTimeUtil.ZERO)
    class Prepared : PlayerState(DateTimeUtil.ZERO)
    class Paused(progress: String) : PlayerState(progress)
    class Playing(progress: String) : PlayerState(progress)

}