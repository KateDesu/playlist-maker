package com.practicum.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.domain.api.ThemeSettingsInteractor

const val PLAYLISTMAKER_PREFERENCES = "playlistmaker_preferences"

class App: Application() {
    val themeSettingsInteractor: ThemeSettingsInteractor by lazy {
        Creator.provideThemeSettingsInteractor(this)
    }

    override fun onCreate() {
        super.onCreate()
        val isDarkTheme = themeSettingsInteractor.isDarkTheme()
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}