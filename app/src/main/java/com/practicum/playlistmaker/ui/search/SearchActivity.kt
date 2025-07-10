package com.practicum.playlistmaker.ui.search

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.PLAYLISTMAKER_PREFERENCES
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.tracks.TrackActivity
import com.practicum.playlistmaker.ui.tracks.TracksAdapter

const val HISTORY_TRACKS_KEY = "history_tracks_key"

class SearchActivity : AppCompatActivity() {
    private var searchInput: String? = null

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { searchTracks() }

    private lateinit var clearButton: ImageView
    private lateinit var searchEditText: EditText

    private lateinit var recyclerViewTracks: RecyclerView
    private val tracks = ArrayList<Track>()
    private lateinit var tracksAdapter: TracksAdapter

    private lateinit var viewHistoryTracks: LinearLayout
    private lateinit var recyclerViewTracksHistory: RecyclerView
    private var tracksHistory = ArrayList<Track>()
    private lateinit var tracksHistoryAdapter: TracksAdapter

    private lateinit var placeholderViewNoInternet: LinearLayout
    private lateinit var placeholderViewNothingFound: LinearLayout
    private lateinit var placeholderButton: Button

    private lateinit var clearHistoryButton: Button
    private lateinit var flProgressBar: FrameLayout

    private lateinit var tracksInteractor: TracksInteractor
    private lateinit var searchHistoryInteractor: SearchHistoryInteractor

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // настройка Tollbar  с кнопкой "Назад"
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        clearButton = findViewById(R.id.ivClearIcon)
        searchEditText = findViewById(R.id.etSearch)
        recyclerViewTracks = findViewById(R.id.rvTrackList)

        placeholderViewNoInternet = findViewById(R.id.llPlaceholderNoInternet)
        placeholderViewNothingFound = findViewById(R.id.llPlaceholderNothingFound)
        placeholderButton = findViewById(R.id.btnUpdate)

        viewHistoryTracks = findViewById(R.id.llTracksHistory)
        recyclerViewTracksHistory = findViewById(R.id.rvTracksHistory)
        clearHistoryButton = findViewById(R.id.btnClearHistory)

        flProgressBar = findViewById(R.id.flProgressBar)

        searchHistoryInteractor = Creator.provideSearchHistoryInteractor(this)

        // настройка RecyclerViews и адаптеров для треков и истории поиска
        tracksAdapter= TracksAdapter { track ->
            Log.d("listener", "tracksAdapter")
            searchHistoryInteractor.addTrack(track)

            val gson = Gson()
            val trackJsonString = gson.toJson(track)
            val intent = Intent(this, TrackActivity::class.java).apply {
                putExtra("trackJson", trackJsonString)
            }
            startActivity(intent)
        }
        tracksAdapter.tracks = tracks
        recyclerViewTracks.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerViewTracks.adapter = tracksAdapter

        tracksHistoryAdapter = TracksAdapter { track ->
            Log.d("listener", "tracksHistoryAdapter")
            searchHistoryInteractor.addTrack(track)

            val gson = Gson()
            val trackJsonString = gson.toJson(track)
            val intent = Intent(this, TrackActivity::class.java).apply {
                putExtra("trackJson", trackJsonString)
            }
            startActivity(intent)
        }
        searchHistoryInteractor.getTracksHistory(object : SearchHistoryInteractor.SearchConsumer {
            override fun consume(history: List<Track>) {
                Log.d("listener", "searchHistoryInteractor ${history}")
                tracksHistory.clear()
                tracksHistory.addAll(history)
                Log.d("listener", "tracksHistory ${tracksHistory.size}")
                tracksHistoryAdapter.notifyDataSetChanged()
            }
        })

        recyclerViewTracksHistory.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerViewTracksHistory.adapter = tracksHistoryAdapter

        // обработка текста в поисковом поле с debounce для поиска треков
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                clearButton.visibility =
                    if (s.isEmpty()) View.GONE else View.VISIBLE

                viewHistoryTracks.visibility =
                    if (searchEditText.hasFocus() && s.isEmpty() && tracksHistory.isNotEmpty()) View.VISIBLE else View.GONE

                searchInput = s.toString()
                searchDebounce()
            }

            override fun afterTextChanged(s: Editable) {}
        })

        clearButton.setOnClickListener {
            searchEditText.setText("")
            clearButton.setVisibility(View.GONE)
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)

            tracks.clear()
            tracksAdapter.notifyDataSetChanged()
            showMessage("", "")
        }

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            viewHistoryTracks.visibility =
                if (hasFocus && searchEditText.text.isEmpty() && tracksHistory.isNotEmpty()) View.VISIBLE else View.GONE
        }

        placeholderButton.setOnClickListener {
            searchTracks()
        }

        // очистка истории поиска
        clearHistoryButton.setOnClickListener {
            searchHistoryInteractor.clearHistory()
            tracksHistory.clear()
            tracksHistoryAdapter.notifyDataSetChanged()
            viewHistoryTracks.visibility = View.GONE
        }
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun searchTracks() {
        flProgressBar.visibility = View.VISIBLE
        placeholderViewNoInternet.visibility = View.GONE
        placeholderViewNothingFound.visibility = View.GONE

        tracksInteractor = Creator.provideTracksInteractor()

        tracksInteractor.searchTracks(
            searchEditText.text.toString(),
            object : TracksInteractor.TracksConsumer {
                @SuppressLint("NotifyDataSetChanged")
                override fun consume(foundTracks: List<Track>) {
                    handler.post {
                        flProgressBar.visibility = View.GONE
                        tracks.clear()
                        if (foundTracks.isNotEmpty()) {
                            tracks.addAll(foundTracks)
                            tracksAdapter.notifyDataSetChanged()
                            placeholderViewNothingFound.visibility = View.GONE
                            showMessage("", "")
                        } else {
                            placeholderViewNothingFound.visibility = View.VISIBLE
                            showMessage(getString(R.string.nothing_found), "")
                        }
                    }
                }
            })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showMessage(text: String, additionalMessage: String) {
        if (text.isNotEmpty()) {
            tracks.clear()
            tracksAdapter.notifyDataSetChanged()

            if (additionalMessage.isNotEmpty()) {
                Toast.makeText(applicationContext, additionalMessage, Toast.LENGTH_LONG)
                    .show()
            }
        } else {
            placeholderViewNoInternet.visibility = View.GONE
            placeholderViewNothingFound.visibility = View.GONE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_SEARCH_INPUT, searchInput)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchInput = savedInstanceState.getString(KEY_SEARCH_INPUT)
    }

    companion object {
        private const val KEY_SEARCH_INPUT = "search_input"

        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}