package com.practicum.playlistmaker

import android.app.Application.MODE_PRIVATE
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TracksAdapter(
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var tracks = ArrayList<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TracksViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_view_track, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as TracksViewHolder
        holder.bind(tracks[position])

        holder.itemView.setOnClickListener {
            val track = tracks[position]

            val preferences = holder.itemView.context.getSharedPreferences(PLAYLISTMAKER_PREFERENCES, MODE_PRIVATE)

            val searchHistory = SearchHistory(preferences)

            searchHistory.addTrack(track)
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }
}