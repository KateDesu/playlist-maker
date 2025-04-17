package com.practicum.playlistmaker

import android.view.RoundedCorner
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TracksViewHolder(trackView: View) : RecyclerView.ViewHolder(trackView) {
    private val trackName: TextView = itemView.findViewById(R.id.textview_track_name)
    private val artistName: TextView = itemView.findViewById(R.id.textview_artist_name)
    private val trackTime: TextView = itemView.findViewById(R.id.textview_track_time)
    private val artworkUrl100: ImageView = itemView.findViewById(R.id.imageview_artwork_url100)

    fun bind(model: Track) {
        trackName.text = model.trackName
        artistName.text = model.artistName
        trackTime.text = model.trackNTime

        Glide.with(itemView.context)
            .load(model.artworkUrl100)
            .centerCrop()
            .transform(RoundedCorners(2))
            .placeholder(R.drawable.placeholder)
            .into(artworkUrl100)
    }
}