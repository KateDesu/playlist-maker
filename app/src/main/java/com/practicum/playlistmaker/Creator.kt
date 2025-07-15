package com.practicum.playlistmaker

import android.content.Context
import com.practicum.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.practicum.playlistmaker.data.repository.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.data.repository.TracksRepositoryImpl
import com.practicum.playlistmaker.data.dto.SearchHistory
import com.practicum.playlistmaker.data.network.ITunesSearchApi
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.impl.TracksInteractorImpl
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Creator {

    private fun getTracksRepository(): TracksRepository {
        val itunesBaseUrl = "https://itunes.apple.com/"
        val retrofit = Retrofit.Builder()
            .baseUrl(itunesBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val iTunesService = retrofit.create(ITunesSearchApi::class.java)
        return TracksRepositoryImpl(RetrofitNetworkClient(iTunesService))
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    private fun getSearchHistoryRepository(context: Context): SearchHistoryRepository {
        val sharedPrefs = context.applicationContext.getSharedPreferences(PLAYLISTMAKER_PREFERENCES, Context.MODE_PRIVATE)
        val searchHistory = SearchHistory(sharedPrefs)
        return SearchHistoryRepositoryImpl(searchHistory)
    }

    fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(getSearchHistoryRepository(context))
    }
}