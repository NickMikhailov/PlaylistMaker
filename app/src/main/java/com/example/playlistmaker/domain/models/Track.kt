package com.example.playlistmaker.domain.models

data class Track (
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
)
{
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Track
        return trackId == other.trackId
    }

    override fun hashCode(): Int {
        return trackId.hashCode()
    }
    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")
}


