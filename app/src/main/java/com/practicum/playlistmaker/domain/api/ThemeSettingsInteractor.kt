package com.practicum.playlistmaker.domain.api

interface ThemeSettingsInteractor {
    fun isDarkTheme(): Boolean
    fun setDarkTheme(isDark: Boolean)
}