package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track

interface SearchHistoryRepository {
    fun addTrack(track: Track)
    fun getTracksHistory(): List<Track>
    fun clearHistory()
}