package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.models.TrackSearchResult

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {

    override fun searchTracks(expression: String, consumer: (TrackSearchResult) -> Unit) {
        val t = Thread {
            consumer(repository.searchTracks(expression))
        }
        t.start()
    }
}