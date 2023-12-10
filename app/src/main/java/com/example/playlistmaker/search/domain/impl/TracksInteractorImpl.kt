package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.TracksConsumer
import com.example.playlistmaker.search.domain.TracksInteractor
import com.example.playlistmaker.search.domain.TracksRepository
import java.util.concurrent.Executors

class TracksInteractorImpl (private val repository: TracksRepository) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun search(expression: String, consumer: TracksConsumer) {
        executor.execute {
            val foundTracks =repository.getTracks(expression)
            consumer.consume(foundTracks)
        }
    }
}