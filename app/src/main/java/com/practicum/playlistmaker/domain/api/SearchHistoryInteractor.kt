package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track

interface SearchHistoryInteractor {
    fun addTrack(track: Track)
    fun getTracksHistory(consumer: SearchConsumer)
    fun clearHistory()

    interface SearchConsumer {
        fun consume(history: List<Track>)
    }
}