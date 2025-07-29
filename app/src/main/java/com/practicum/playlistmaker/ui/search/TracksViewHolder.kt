package com.practicum.playlistmaker.ui.search

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ItemViewTrackBinding
import com.practicum.playlistmaker.domain.models.Track
import kotlin.math.roundToInt

class TracksViewHolder(
    val binding: ItemViewTrackBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(model: Track) {
        binding.textviewTrackName.text = model.trackName
        binding.textviewArtistName.text = model.artistName
        binding.textviewTrackTime.text = model.trackTime

        val radius = 2 * itemView.context.resources.displayMetrics.density + 0.5f

        Glide.with(itemView.context)
            .load(model.artworkUrl100)
            .centerCrop()
            .transform(RoundedCorners(radius.roundToInt()))
            .placeholder(R.drawable.placeholder_track)
            .into(binding.imageviewArtworkUrl100)
    }
}