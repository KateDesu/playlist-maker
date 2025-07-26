package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.ThemeSettingsInteractor
import com.practicum.playlistmaker.domain.api.ThemeSettingsRepository

class ThemeSettingsInteractorImpl(private val repository: ThemeSettingsRepository) :
    ThemeSettingsInteractor {
    override fun isDarkTheme() = repository.getThemeSettings()
    override fun setDarkTheme(isDark: Boolean) = repository.saveThemeSettings(isDark)
}