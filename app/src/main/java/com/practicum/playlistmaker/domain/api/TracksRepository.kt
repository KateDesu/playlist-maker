package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.models.TrackSearchResult

interface TracksRepository {
    fun searchTracks(expression: String): TrackSearchResult
}