package com.example.playlistmaker.library.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracks_table")
data class TrackEntity (
    @PrimaryKey
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
    val isFavorite: Boolean,
    val dateTime: Long
)

