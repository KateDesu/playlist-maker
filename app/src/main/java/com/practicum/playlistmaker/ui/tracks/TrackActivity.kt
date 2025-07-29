package com.practicum.playlistmaker.ui.tracks

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityTrackBinding
import com.practicum.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.roundToInt

class TrackActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrackBinding
    private var mediaPlayer = MediaPlayer()

    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    private var playerState = STATE_DEFAULT

    private var url: String? = null

    private val handler = Handler(Looper.getMainLooper())

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
        url = track.previewUrl

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

        preparePlayer()

        binding.ibPlayStop.setOnClickListener {
            playbackControl()
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        stopTimer()
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            binding.ibPlayStop.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            binding.ibPlayStop.setImageResource(R.drawable.ic_play_track_button)
            playerState = STATE_PREPARED

            stopTimer()
            binding.tvTrackTime.text = "00:00"
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        binding.ibPlayStop.setImageResource(R.drawable.ic_stop_track_button)
        playerState = STATE_PLAYING

        startTimer()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        binding.ibPlayStop.setImageResource(R.drawable.ic_play_track_button)
        playerState = STATE_PAUSED
    }

    private fun startTimer() {
        updateTrackTime()
    }

    private fun updateTrackTime() {
        if (mediaPlayer.isPlaying) {
            binding.tvTrackTime.text = dateFormat.format(mediaPlayer.currentPosition)
            handler.postDelayed({
                updateTrackTime() // рекурсивно вызываем метод для обновления времени
            }, UPDATE_INTERVAL_MS) // задержка в 300 мс
        }
    }

    private fun stopTimer() {
        handler.removeCallbacksAndMessages(null)
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val UPDATE_INTERVAL_MS = 300L
    }
}