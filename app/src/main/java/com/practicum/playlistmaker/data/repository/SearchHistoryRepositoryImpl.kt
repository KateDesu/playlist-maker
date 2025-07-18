package com.practicum.playlistmaker.data.repository

import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.domain.models.Track

class SearchHistoryRepositoryImpl(
    private val sharedPrefs: SharedPreferences
) : SearchHistoryRepository {
    private val gson = Gson()

    override fun addTrack(track: Track) {
        val historyTracks = getHistoryList().toMutableList()
        historyTracks.removeAll { it.trackId == track.trackId }
        historyTracks.add(0, track)
        if (historyTracks.size > MAX_HISTORY_SIZE) {
            historyTracks.subList(MAX_HISTORY_SIZE, historyTracks.size).clear()
        }
        saveHistoryList(historyTracks)
        Log.d("SearchHistoryRepo", "addTrack: saved history = ${gson.toJson(historyTracks)}")
    }

    override fun clearHistory() {
        sharedPrefs.edit().remove(HISTORY_TRACKS_KEY).apply()
    }

    override fun getTracksHistory(): List<Track> {
        //return getHistoryList()
        val history = getHistoryList()
        Log.d("SearchHistoryRepo", "getTracksHistory: loaded history = ${gson.toJson(history)}")
        return history
    }

    private fun getHistoryList(): List<Track> {
        val json = sharedPrefs.getString(HISTORY_TRACKS_KEY, null) ?: return emptyList()
        val type = object : TypeToken<List<Track>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    private fun saveHistoryList(history: List<Track>) {
        val json = gson.toJson(history)
        sharedPrefs.edit().putString(HISTORY_TRACKS_KEY, json).apply()
    }

    companion object {
        private const val HISTORY_TRACKS_KEY = "history_tracks_key"
        private const val MAX_HISTORY_SIZE = 10
    }
}