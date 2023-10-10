package com.example.playlistmaker.data.dto

class TrackSearchResponse (val resultsCount: Int,
                           val results: List<TrackDto>) : Response()