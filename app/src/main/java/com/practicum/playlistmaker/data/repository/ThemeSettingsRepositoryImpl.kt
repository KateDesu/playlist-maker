package com.practicum.playlistmaker.data.repository

import android.content.SharedPreferences
import com.practicum.playlistmaker.domain.api.ThemeSettingsRepository

class ThemeSettingsRepositoryImpl (
    private val sharedPreferences: SharedPreferences
) : ThemeSettingsRepository {
    override fun getThemeSettings(): Boolean {
        return sharedPreferences.getBoolean("DARK_THEME_KEY", false)
    }
    override fun saveThemeSettings(isDarkTheme: Boolean) {
        sharedPreferences.edit().putBoolean("DARK_THEME_KEY", isDarkTheme).apply()
    }
}