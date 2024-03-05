package com.example.playlistmaker.player.domain.models

data class Track (
    val trackId: Int,   //Id
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTime: String, // Продолжительность трека
    val artworkUrl100: String, // Ссылка на изображение обложки
    val artworkUrl500: String, // Ссылка на изображение обложки
    val collectionName: String, //Альбом
    val releaseDate: String, //Дата выхода песни
    val primaryGenreName: String, //Жанр
    val country: String, //Страна
    val previewUrl: String, //Ссылка на аудио
    var isFavorite: Boolean //Наличие в избранном
)


