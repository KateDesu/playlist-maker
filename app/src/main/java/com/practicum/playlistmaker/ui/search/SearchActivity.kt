package com.practicum.playlistmaker.ui.search

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.presentation.search.SearchState
import com.practicum.playlistmaker.presentation.search.SearchViewModel
import com.practicum.playlistmaker.ui.tracks.TrackActivity

class SearchActivity : AppCompatActivity() {
    private var viewModel: SearchViewModel? = null

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

    private val searchHistoryInteractor: SearchHistoryInteractor by lazy {
        Creator.provideSearchHistoryInteractor(this)
    }

    private var textWatcher: TextWatcher? = null

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

        // настройка RecyclerViews и адаптеров для треков и истории поиска
        tracksAdapter = TracksAdapter(tracks) { track ->
            searchHistoryInteractor.addTrack(track)

            val gson = Gson()
            val trackJsonString = gson.toJson(track)
            val intent = Intent(this, TrackActivity::class.java).apply {
                putExtra("trackJson", trackJsonString)
            }
            startActivity(intent)
        }
        recyclerViewTracks.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerViewTracks.adapter = tracksAdapter

        tracksHistoryAdapter = TracksAdapter(tracksHistory) { track ->
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
                tracksHistory.clear()
                tracksHistory.addAll(history)
                tracksHistoryAdapter.setTracks(tracksHistory)
            }
        })

        recyclerViewTracksHistory.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerViewTracksHistory.adapter = tracksHistoryAdapter

        // обработка текста в поисковом поле с debounce для поиска треков
        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility =
                    if (s.toString().isEmpty()) View.GONE else View.VISIBLE

                if (searchEditText.hasFocus() && s.toString().isEmpty()) {
                    viewModel?.showHistory()
                } else {
                    viewModel?.searchDebounce(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        textWatcher?.let { searchEditText.addTextChangedListener(it) }

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
            if (hasFocus && searchEditText.text.isEmpty()) {
                viewModel?.showHistory()
            }
        }

        placeholderButton.setOnClickListener {
            viewModel?.repeatLastSearch()
        }

        // очистка истории поиска
        clearHistoryButton.setOnClickListener {
            searchHistoryInteractor.clearHistory()
            tracksHistory.clear()
            tracksHistoryAdapter.setTracks(tracksHistory)
            viewHistoryTracks.visibility = View.GONE
        }

        viewModel = ViewModelProvider(this, SearchViewModel.getFactory())
            .get(SearchViewModel::class.java)

        viewModel?.observeState()?.observe(this) {
            render(it)
        }
        viewModel?.observeToast()?.observe(this) {
            showToast(it)
        }
    }

    override fun onResume() {
        super.onResume()
        searchHistoryInteractor.getTracksHistory(object : SearchHistoryInteractor.SearchConsumer {
            override fun consume(history: List<Track>) {
                tracksHistory.clear()
                tracksHistory.addAll(history)
                tracksHistoryAdapter.setTracks(tracksHistory)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        textWatcher?.let { searchEditText.removeTextChangedListener(it) }
    }

    fun showLoading() {
        flProgressBar.visibility = View.VISIBLE
        placeholderViewNoInternet.visibility = View.GONE
        placeholderViewNothingFound.visibility = View.GONE
    }

    @SuppressLint("NotifyDataSetChanged")
    fun showContent(tracks: List<Track>) {
        flProgressBar.visibility = View.GONE
        placeholderViewNoInternet.visibility = View.GONE
        placeholderViewNothingFound.visibility = View.GONE
        if (searchEditText.text.isEmpty()) {
            // Показываем ИСТОРИЮ
            viewHistoryTracks.visibility = View.VISIBLE
            clearHistoryButton.visibility = View.VISIBLE
            recyclerViewTracks.visibility = View.GONE

            // Обновляем адаптер истории
            tracksHistory.clear()
            tracksHistory.addAll(tracks)
            tracksHistoryAdapter.setTracks(tracksHistory)
        } else {
            // Показываем РЕЗУЛЬТАТЫ ПОИСКА
            viewHistoryTracks.visibility = View.GONE
            recyclerViewTracks.visibility = View.VISIBLE

            this.tracks.clear()
            this.tracks.addAll(tracks)
            tracksAdapter.notifyDataSetChanged()
        }
    }

    fun showError(message: String) {
        flProgressBar.visibility = View.GONE
        placeholderViewNoInternet.visibility = View.GONE
        placeholderViewNothingFound.visibility = View.GONE
        showMessage(message, "")
    }

    fun showEmpty(message: String) {
        flProgressBar.visibility = View.GONE
        placeholderViewNoInternet.visibility = View.GONE
        placeholderViewNothingFound.visibility = View.VISIBLE
        showMessage("", message)
    }

    fun showNoInternet() {
        flProgressBar.visibility = View.GONE
        placeholderViewNoInternet.visibility = View.VISIBLE
        placeholderViewNothingFound.visibility = View.GONE
        recyclerViewTracks.visibility = View.GONE
        if (searchEditText.text.isEmpty()) {
            viewHistoryTracks.visibility = View.VISIBLE
            clearHistoryButton.visibility = View.VISIBLE
        } else {
            viewHistoryTracks.visibility = View.GONE
        }
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
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
    }

    fun showToast(message: String?) {
        message?.let { Toast.makeText(this, it, Toast.LENGTH_LONG).show() }
    }

    fun render(state: SearchState) {
        when (state) {
            is SearchState.Loading -> showLoading()
            is SearchState.Content -> showContent(state.tracks)
            is SearchState.Error -> showError(state.errorMessage)
            is SearchState.NoInternet -> {
                showNoInternet()
            }
            is SearchState.Empty -> showEmpty(state.message)
        }
    }
}