package com.practicum.playlistmaker

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

//class TracksViewHolder(trackView: View) :
//    RecyclerView.ViewHolder(trackView) {
class TracksViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view_track, parent, false)) {

    private val trackName: TextView = itemView.findViewById(R.id.textview_track_name)
    private val artistName: TextView = itemView.findViewById(R.id.textview_artist_name)
    private val trackTime: TextView = itemView.findViewById(R.id.textview_track_time)
    private val artworkUrl100: ImageView = itemView.findViewById(R.id.imageview_artwork_url100)

    fun bind(model: Track) {
        trackName.text = model.trackName
        artistName.text = model.artistName

        trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(model.trackTime.toLong())
        Log.d("trackTime", trackTime.text.toString())

        Glide.with(itemView.context)
            .load(model.artworkUrl100)
            .centerCrop()
            .transform(RoundedCorners(2))
            .placeholder(R.drawable.placeholder)
            .into(artworkUrl100)
    }
}