package com.practicum.playlistmaker.data.repository

import com.practicum.playlistmaker.data.NetworkClient
import com.practicum.playlistmaker.data.dto.TracksSearchResponse
import com.practicum.playlistmaker.data.dto.TracksSearchRequest
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.models.TrackSearchResult
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTracks(expression: String): TrackSearchResult {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        return when (response.resultCode) {
            200 -> TrackSearchResult.Success((response as TracksSearchResponse).results.map {
                Track(
                    it.trackId,
                    it.trackName,
                    it.artistName,
                    formatTimeMillis(it.trackTimeMillis),
                    it.artworkUrl100,
                    it.collectionName,
                    it.releaseDate,
                    it.primaryGenreName,
                    it.country,
                    it.previewUrl
                )
            })

            -1 -> TrackSearchResult.NoInternet
            else -> TrackSearchResult.NotFound
        }
    }

    private fun formatTimeMillis(durationMillis: Long): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault())
            .format(Date(durationMillis))
    }
}