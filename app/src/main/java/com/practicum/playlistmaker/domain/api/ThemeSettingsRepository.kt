package com.practicum.playlistmaker.domain.api

interface ThemeSettingsRepository {
    fun getThemeSettings(): Boolean
    fun saveThemeSettings(isDarkTheme: Boolean)
}