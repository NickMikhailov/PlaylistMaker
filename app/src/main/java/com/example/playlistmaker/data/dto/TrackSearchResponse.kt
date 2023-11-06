package com.example.playlistmaker.data.dto

class TrackSearchResponse (val resultsCount: Int,
                           val searchType: String,
                           val expression: String,
                           val results: List<TrackDto>) : Response()