package com.practicum.playlistmaker

import com.google.gson.annotations.SerializedName

data class Track (
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    @SerializedName("trackTimeMillis") val trackTime: String,
    val artworkUrl100: String
)