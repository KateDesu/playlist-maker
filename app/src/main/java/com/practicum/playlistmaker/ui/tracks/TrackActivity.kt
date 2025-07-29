package com.practicum.playlistmaker.ui.tracks

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityTrackBinding
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.presentation.player.PlayerViewModel
import kotlin.math.roundToInt

class TrackActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrackBinding
    private lateinit var playerViewModel: PlayerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityTrackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Включаем кнопку "Назад" в Toolbar
        binding.ibToolbar.setOnClickListener {
            finish()
        }

        val trackJson = intent.getStringExtra("trackJson")
        val gson = Gson()
        val track: Track = gson.fromJson(trackJson, Track::class.java)
        val url = track.previewUrl

        val enlargedImageUrl = track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
        val radius = 8 * this.resources.displayMetrics.density + 0.5f

        Glide.with(this)
            .load(enlargedImageUrl)
            .centerCrop()
            .transform(RoundedCorners(radius.roundToInt()))
            .placeholder(R.drawable.placeholder_track_big)
            .into(binding.ivCover)

        binding.tvTrackName.text = track.trackName
        binding.tvArtistName.text = track.artistName
        binding.tvDurationValue.text = track.trackTime
        binding.tvCollectionNameValue.text = track.collectionName
        binding.tvReleaseDateValue.text = track.releaseDate.substring(0, 4)
        binding.tvPrimaryGenreNameValue.text = track.primaryGenreName
        binding.tvCountryValue.text = track.country

        playerViewModel = ViewModelProvider(this, PlayerViewModel.getFactory(url ?: ""))[PlayerViewModel::class.java]

        playerViewModel.observePlayerState().observe(this) { state ->
            when (state) {
                PlayerViewModel.STATE_PREPARED -> {
                    binding.ibPlayStop.isEnabled = true
                    binding.ibPlayStop.setImageResource(R.drawable.ic_play_track_button)
                }
                PlayerViewModel.STATE_PLAYING -> {
                    binding.ibPlayStop.setImageResource(R.drawable.ic_stop_track_button)
                }
                PlayerViewModel.STATE_PAUSED, PlayerViewModel.STATE_DEFAULT -> {
                    binding.ibPlayStop.setImageResource(R.drawable.ic_play_track_button)
                }
            }
        }

        playerViewModel.observeProgressTime().observe(this) { time ->
            binding.tvTrackTime.text = time
        }

        binding.ibPlayStop.setOnClickListener {
            playerViewModel.onPlayButtonClicked()
        }
    }

    override fun onPause() {
        super.onPause()
        playerViewModel.onPause()
    }
}