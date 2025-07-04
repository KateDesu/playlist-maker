package com.practicum.playlistmaker.data.dto

class TracksResponse(
    val searchType: String,
    val expression: String,
    val results: List<TrackDto>
) : Response()