package com.practicum.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.domain.models.Track

class SearchHistory(private val sharedPrefs: SharedPreferences) {

    private val gson = Gson()

    fun addTrack(track: Track) {
        val editor = sharedPrefs.edit()

        val jsonTracks = sharedPrefs.getString(HISTORY_TRACKS_KEY, null)
        var historyTracks: ArrayList<Track> = gson.fromJson(jsonTracks, object : TypeToken<ArrayList<Track>>() {}.type) ?: ArrayList()

        historyTracks.removeIf { it == track }

        historyTracks.add(0, track)

        if (historyTracks.size > 10) {
            historyTracks = ArrayList(historyTracks.subList(0, 10))
        }

        val updatedJsonTracks = gson.toJson(historyTracks)
        editor.putString(HISTORY_TRACKS_KEY, updatedJsonTracks)
        editor.apply()
    }

    fun clearHistory() {
        with(sharedPrefs.edit()) {
            remove(HISTORY_TRACKS_KEY)
            commit()
        }
    }

    fun getTracksHistory(): ArrayList<Track> {
        val stringHistory = sharedPrefs.getString(HISTORY_TRACKS_KEY, null)
        return if (stringHistory != null) {
            val type = object : TypeToken<ArrayList<Track>>() {}.type
            gson.fromJson(stringHistory, type) ?: ArrayList()
        } else {
            ArrayList()
        }
    }
}