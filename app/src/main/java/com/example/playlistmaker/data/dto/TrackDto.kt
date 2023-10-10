package com.example.playlistmaker.data.dto

data class TrackDto (    val trackId: Int,   //Id
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