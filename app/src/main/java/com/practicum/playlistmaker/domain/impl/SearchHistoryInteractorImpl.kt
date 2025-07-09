package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.domain.models.Track

class SearchHistoryInteractorImpl(private val repository: SearchHistoryRepository) : SearchHistoryInteractor {
    override fun addTrack(track: Track) {
        repository.addTrack(track)
    }

    override fun getTracksHistory(consumer: SearchHistoryInteractor.SearchConsumer) {
        val t = Thread {
            consumer.consume(repository.getTracksHistory())
        }
        t.start()
    }

    override fun clearHistory() {
        repository.clearHistory()
    }
}