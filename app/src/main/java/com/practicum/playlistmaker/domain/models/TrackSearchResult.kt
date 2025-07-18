package com.practicum.playlistmaker.domain.models

sealed class TrackSearchResult {
    data class Success(val tracks: List<Track>) : TrackSearchResult()
    object NoInternet : TrackSearchResult()
    object NotFound : TrackSearchResult()
}