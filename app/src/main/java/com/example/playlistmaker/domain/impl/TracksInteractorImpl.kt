package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import java.util.concurrent.Executors

class TracksInteractorImpl (private val repository: TracksRepository) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun search(expression: String, consumer: TracksInteractor.TracksConsumer) {
        val foundTrack =repository.getTracks(expression)
        executor.execute {
            consumer.consume(foundTrack)
        }
    }
}