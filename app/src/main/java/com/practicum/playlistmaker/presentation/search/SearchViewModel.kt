package com.practicum.playlistmaker.presentation.search

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.domain.api.TracksInteractor
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import com.practicum.playlistmaker.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.domain.models.TrackSearchResult

class SearchViewModel(private val context: Context) : ViewModel() {
    private val tracksInteractor: TracksInteractor by lazy {
        Creator.provideTracksInteractor()
    }
    private val searchHistoryInteractor: SearchHistoryInteractor =
        Creator.provideSearchHistoryInteractor(context)

    private val handler = Handler(Looper.getMainLooper())
    private var latestSearchText: String? = null
    private var lastSearchQuery: String? = null

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData

    private val toastLiveEvent = SingleLiveEvent<String>()
    fun observeToast(): LiveData<String> = toastLiveEvent

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }
        this.latestSearchText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        val searchRunnable = Runnable { searchRequest(changedText) }
        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(searchRunnable, SEARCH_REQUEST_TOKEN, postTime)
    }

    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            lastSearchQuery = newSearchText // Сохраняем последний запрос
            renderState(SearchState.Loading)
            tracksInteractor.searchTracks(newSearchText) { result ->
                when (result) {
                    is TrackSearchResult.Success -> {
                        if (result.tracks.isEmpty()) {
                            renderState(SearchState.Empty)
                        } else {
                            renderState(SearchState.Content(result.tracks))
                        }
                    }

                    TrackSearchResult.NoInternet -> {
                        renderState(SearchState.NoInternet)
                    }

                    TrackSearchResult.NotFound -> {
                        renderState(SearchState.Empty)
                    }
                }
            }
        }
    }
    fun repeatLastSearch() {
        lastSearchQuery?.let { query ->
            searchRequest(query)
        }
    }

    fun showHistory() {
        searchHistoryInteractor.getTracksHistory(object :
            SearchHistoryInteractor.SearchConsumer {
            override fun consume(history: List<com.practicum.playlistmaker.domain.models.Track>) {
                renderState(SearchState.Content(history))
            }
        })
    }

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
        if (state is SearchState.Error) {
            toastLiveEvent.postValue(state.errorMessage)
        }
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()

        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as Application)
                SearchViewModel(app)
            }
        }
    }
}