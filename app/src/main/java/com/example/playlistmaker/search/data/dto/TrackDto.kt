package com.example.playlistmaker.search.data.dto

import com.example.playlistmaker.main.domain.models.DateTimeUtil
import com.example.playlistmaker.player.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

data class TrackDto(
    val trackId: Int,   //Id
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTimeMillis: String, // Продолжительность трека
    val artworkUrl100: String, // Ссылка на изображение обложки
    val collectionName: String, //Альбом
    val releaseDate: String, //Дата выхода песни
    val primaryGenreName: String, //Жанр
    val country: String, //Страна
    val previewUrl: String //Ссылка на аудио
) {
    fun getTrack(): Track {
        return Track(
            trackId,
            trackName,
            artistName,
            formatTime(),
            artworkUrl100,
            getCoverArtwork500(),
            collectionName,
            releaseDate,
            primaryGenreName,
            country,
            previewUrl,
            false
        )
    }


    private fun getCoverArtwork500() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
    private fun formatTime() =
        SimpleDateFormat(DateTimeUtil.FORMAT_MINUTES_SECONDS, Locale.getDefault()).format(
            trackTimeMillis.toInt()
        )

}