package com.practicum.playlistmaker.ui.tracks

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.roundToInt

class TrackActivity : AppCompatActivity() {

    private lateinit var ibPlay: ImageButton
    private var mediaPlayer = MediaPlayer()

    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    private var playerState = STATE_DEFAULT

    private var url: String?=null

    private val handler = Handler(Looper.getMainLooper())
    private  lateinit var tvTrackTime: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_track)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Включаем кнопку "Назад" в Toolbar
        val toolbar = findViewById<ImageButton>(R.id.ibToolbar)
        toolbar.setOnClickListener {
            finish()
        }

        ibPlay = findViewById(R.id.ibPlayStop)

        val trackJson = intent.getStringExtra("trackJson")
        val gson = Gson()
        val track: Track = gson.fromJson(trackJson, Track::class.java)
        Log.d("trackJson", trackJson.toString())
        url = track.previewUrl

        val ivCover = findViewById<ImageView>(R.id.ivCover)
        val tvTrackName = findViewById<TextView>(R.id.tvTrackName)
        val tvArtistName = findViewById<TextView>(R.id.tvArtistName)
        val tvDurationValue = findViewById<TextView>(R.id.tvDurationValue)
        val tvCollectionNameValue = findViewById<TextView>(R.id.tvСollectionNameValue)
        val tvReleaseDateValue = findViewById<TextView>(R.id.tvReleaseDateValue)
        val tvPrimaryGenreNameValue = findViewById<TextView>(R.id.tvPrimaryGenreNameValue)
        val tvCountryValue = findViewById<TextView>(R.id.tvCountryValue)
        tvTrackTime = findViewById(R.id.tvTrackTime)

        val enlargedImageUrl = track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")

        val radius = 8 * this.getResources().displayMetrics.density + 0.5f

        Glide.with(this)
            .load(enlargedImageUrl)
            .centerCrop()
            .transform(RoundedCorners(radius.roundToInt()))
            .placeholder(R.drawable.placeholder_track_big)
            .into(ivCover)

        tvTrackName.text = track.trackName
        tvArtistName.text = track.artistName
        tvDurationValue.text =  SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTime.toLong())
        tvCollectionNameValue.text = track.collectionName
        tvReleaseDateValue.text = track.releaseDate.substring(0, 4)
        tvPrimaryGenreNameValue.text = track.primaryGenreName
        tvCountryValue.text = track.country

        preparePlayer()

        ibPlay.setOnClickListener {
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
        when(playerState) {
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
            ibPlay.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            ibPlay.setImageResource(R.drawable.ic_play_track_button)
            Log.d("player", "PLAY")
            playerState = STATE_PREPARED

            stopTimer()
            tvTrackTime.text="00:00"
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        ibPlay.setImageResource(R.drawable.ic_stop_track_button)
        Log.d("player", "PAUSE")
        playerState = STATE_PLAYING

        startTimer()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        ibPlay.setImageResource(R.drawable.ic_play_track_button)
        playerState = STATE_PAUSED
    }

    fun startTimer() {
        updateTrackTime()
    }

    private fun updateTrackTime() {
        if (mediaPlayer.isPlaying) {
            tvTrackTime.text = dateFormat.format(mediaPlayer.currentPosition)
            handler.postDelayed({
                updateTrackTime() // рекурсивно вызываем метод для обновления времени
            }, UPDATE_INTERVAL_MS) // задержка в 300 мс
        }
    }

    fun stopTimer() {
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