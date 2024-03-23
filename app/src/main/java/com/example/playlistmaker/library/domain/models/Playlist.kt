package com.example.playlistmaker.library.domain.models

data class Playlist (
    var id: Long,
    val name: String,
    val description: String,
    val trackListGson: String,
    val trackCount: Int,
    var coverName: String,
    var duration: Long
)