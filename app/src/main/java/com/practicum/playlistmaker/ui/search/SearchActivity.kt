package com.practicum.playlistmaker.ui.search

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.presentation.search.SearchState
import com.practicum.playlistmaker.presentation.search.SearchViewModel
import com.practicum.playlistmaker.ui.tracks.TrackActivity

class SearchActivity : AppCompatActivity() {
    private var viewModel: SearchViewModel? = null
    private lateinit var binding: ActivitySearchBinding

    private val tracks = ArrayList<Track>()
    private lateinit var tracksAdapter: TracksAdapter
    private var tracksHistory = ArrayList<Track>()
    private lateinit var tracksHistoryAdapter: TracksAdapter

    private val searchHistoryInteractor: SearchHistoryInteractor by lazy {
        Creator.provideSearchHistoryInteractor(this)
    }

    private var textWatcher: TextWatcher? = null

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
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
        binding.rvTrackList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvTrackList.adapter = tracksAdapter

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

        binding.rvTracksHistory.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvTracksHistory.adapter = tracksHistoryAdapter

        // обработка текста в поисковом поле с debounce для поиска треков
        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.ivClearIcon.visibility =
                    if (s.toString().isEmpty()) View.GONE else View.VISIBLE

                if (binding.etSearch.hasFocus() && s.toString().isEmpty()) {
                    viewModel?.showHistory()
                } else {
                    viewModel?.searchDebounce(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        textWatcher?.let { binding.etSearch.addTextChangedListener(it) }

        binding.ivClearIcon.setOnClickListener {
            binding.etSearch.setText("")
            binding.ivClearIcon.setVisibility(View.GONE)
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)

            tracks.clear()
            tracksAdapter.notifyDataSetChanged()
            showMessage("", "")
        }

        binding.etSearch.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.etSearch.text.isEmpty()) {
                viewModel?.showHistory()
            }
        }

        binding.btnUpdate.setOnClickListener {
            viewModel?.repeatLastSearch()
        }

        // очистка истории поиска
        binding.btnClearHistory.setOnClickListener {
            searchHistoryInteractor.clearHistory()
            tracksHistory.clear()
            tracksHistoryAdapter.setTracks(tracksHistory)
            binding.llTracksHistory.visibility = View.GONE
        }

        viewModel = ViewModelProvider(this, SearchViewModel.getFactory())[SearchViewModel::class.java]

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
        textWatcher?.let { binding.etSearch.removeTextChangedListener(it) }
    }

    private fun showLoading() {
        binding.apply {
            flProgressBar.visibility = View.VISIBLE
            llPlaceholderNoInternet.visibility = View.GONE
            llPlaceholderNothingFound.visibility = View.GONE
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun showContent(tracks: List<Track>) {
        binding.apply {
            flProgressBar.visibility = View.GONE
            llPlaceholderNoInternet.visibility = View.GONE
            llPlaceholderNothingFound.visibility = View.GONE
        }
        if (binding.etSearch.text.isEmpty()) {
            // Показываем ИСТОРИЮ
            binding.apply {
                llTracksHistory.visibility = View.VISIBLE
                btnClearHistory.visibility = View.VISIBLE
                rvTrackList.visibility = View.GONE
            }

            // Обновляем адаптер истории
            tracksHistory.clear()
            tracksHistory.addAll(tracks)
            tracksHistoryAdapter.setTracks(tracksHistory)
        } else {
            // Показываем РЕЗУЛЬТАТЫ ПОИСКА
            binding.llTracksHistory.visibility = View.GONE
            binding.rvTrackList.visibility = View.VISIBLE

            this.tracks.clear()
            this.tracks.addAll(tracks)
            tracksAdapter.notifyDataSetChanged()
        }
    }

    private fun showError(message: String) {
        binding.apply {
            flProgressBar.visibility = View.GONE
            llPlaceholderNoInternet.visibility = View.GONE
            llPlaceholderNothingFound.visibility = View.GONE
        }
        showMessage(message, "")
    }

    private fun showEmpty() {
        binding.apply {
            flProgressBar.visibility = View.GONE
            llPlaceholderNoInternet.visibility = View.GONE
            llPlaceholderNothingFound.visibility = View.VISIBLE
            rvTrackList.visibility = View.GONE
        }
    }

    private fun showNoInternet() {
        binding.apply {
            flProgressBar.visibility = View.GONE
            llPlaceholderNoInternet.visibility = View.VISIBLE
            llPlaceholderNothingFound.visibility = View.GONE
            rvTrackList.visibility = View.GONE
        }
        if (binding.etSearch.text.isEmpty()) {
            binding.llTracksHistory.visibility = View.VISIBLE
            binding.btnClearHistory.visibility = View.VISIBLE
        } else {
            binding.llTracksHistory.visibility = View.GONE
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
            binding.llPlaceholderNoInternet.visibility = View.GONE
            binding.llPlaceholderNothingFound.visibility = View.GONE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
    }

    private fun showToast(message: String?) {
        message?.let { Toast.makeText(this, it, Toast.LENGTH_LONG).show() }
    }

    private fun render(state: SearchState) {
        when (state) {
            is SearchState.Loading -> showLoading()
            is SearchState.Content -> showContent(state.tracks)
            is SearchState.Error -> showError(state.errorMessage)
            is SearchState.NoInternet -> {
                showNoInternet()
            }
            is SearchState.Empty -> showEmpty()
        }
    }
}