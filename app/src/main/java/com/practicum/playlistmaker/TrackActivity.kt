package com.practicum.playlistmaker

import android.os.Bundle
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
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Locale

class TrackActivity : AppCompatActivity() {

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

        val trackJson = intent.getStringExtra("trackJson")
        val gson = Gson()
        val track: Track = gson.fromJson(trackJson, Track::class.java)
        Log.d("trackJson", trackJson.toString())

        val ivCover = findViewById<ImageView>(R.id.ivCover)
        val tvTrackName = findViewById<TextView>(R.id.tvTrackName)
        val tvArtistName = findViewById<TextView>(R.id.tvArtistName)
        val tvDurationValue = findViewById<TextView>(R.id.tvDurationValue)
        val tvCollectionNameValue = findViewById<TextView>(R.id.tvСollectionNameValue)
        val tvReleaseDateValue = findViewById<TextView>(R.id.tvReleaseDateValue)
        val tvPrimaryGenreNameValue = findViewById<TextView>(R.id.tvPrimaryGenreNameValue)
        val tvCountryValue = findViewById<TextView>(R.id.tvCountryValue)

        val enlargedImageUrl = track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")

        Glide.with(this)
            .load(enlargedImageUrl)
            .centerCrop()
            .transform(RoundedCorners(2))
            .placeholder(R.drawable.placeholder_track_big)
            .into(ivCover)

        /*val year: String = if (track.releaseDate != null) {
            track.releaseDate.year.toString()
        } else {
            ""
        }*/

        tvTrackName.text = track.trackName
        tvArtistName.text = track.artistName
        tvDurationValue.text =  SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTime.toLong())
        tvCollectionNameValue.text = track.collectionName
        tvReleaseDateValue.text = track.releaseDate.substring(0, 4)
        tvPrimaryGenreNameValue.text = track.primaryGenreName
        tvCountryValue.text = track.country
    }
}