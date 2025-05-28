package com.practicum.playlistmaker

import android.app.Application.MODE_PRIVATE
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson

class TracksAdapter(
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var tracks = ArrayList<Track>()

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private var isClickAllowed = true

    private val handler = Handler(Looper.getMainLooper())

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

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

            val preferences = holder.itemView.context.getSharedPreferences(
                PLAYLISTMAKER_PREFERENCES,
                MODE_PRIVATE
            )

            val searchHistory = SearchHistory(preferences)

            searchHistory.addTrack(track)

            val gson = Gson()
            val trackJsonString: String = gson.toJson(track)

            if (clickDebounce()) {
                val trackIntent = Intent(holder.itemView.context, TrackActivity::class.java)

                trackIntent.putExtra("trackJson", trackJsonString)
                holder.itemView.context.startActivity(trackIntent)
            }
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }
}