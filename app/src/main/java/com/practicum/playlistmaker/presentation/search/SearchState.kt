package com.practicum.playlistmaker.presentation.search

import com.practicum.playlistmaker.domain.models.Track

sealed class SearchState {
    object Loading : SearchState()
    data class Error(val errorMessage: String) : SearchState()
    object NoInternet : SearchState()
    data class Empty(val message: String) : SearchState()
    data class Content( val tracks: List<Track>) : SearchState()
}