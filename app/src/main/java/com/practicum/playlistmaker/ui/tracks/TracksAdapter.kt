package com.practicum.playlistmaker.ui.tracks

import android.app.Application.MODE_PRIVATE
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.practicum.playlistmaker.PLAYLISTMAKER_PREFERENCES
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.dto.SearchHistory
import com.practicum.playlistmaker.domain.models.Track

class TracksAdapter(
    private val onItemClickListener: (Track) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var tracks = ArrayList<Track>()

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
        val track = tracks[position]
        holder.bind(track)

        holder.itemView.setOnClickListener {
            if (clickDebounce()) {
                Log.d("listener","Adapter")
                onItemClickListener(track)
            }
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}