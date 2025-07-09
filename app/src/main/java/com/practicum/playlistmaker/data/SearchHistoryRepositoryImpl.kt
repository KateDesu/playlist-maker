package com.practicum.playlistmaker.data

import com.practicum.playlistmaker.data.dto.SearchHistory
import com.practicum.playlistmaker.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.domain.models.Track

class SearchHistoryRepositoryImpl(private val searchHistory: SearchHistory) : SearchHistoryRepository {
    override fun addTrack(track: Track) {
        searchHistory.addTrack(track)
    }

    override fun getTracksHistory(): List<Track> {
        return searchHistory.getTracksHistory()
    }

    override fun clearHistory() {
        searchHistory.clearHistory()
    }
}