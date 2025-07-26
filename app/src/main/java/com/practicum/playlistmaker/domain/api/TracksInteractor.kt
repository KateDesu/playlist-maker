package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.TrackSearchResult

interface TracksInteractor {
    fun searchTracks(expression: String, consumer: (TrackSearchResult) -> Unit)
}