package com.practicum.playlistmaker.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.domain.api.ThemeSettingsInteractor

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    private val themeSettingsInteractor: ThemeSettingsInteractor by lazy {
        Creator.provideThemeSettingsInteractor(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.settings) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.toolbar)

        // Включаем кнопку "Назад" в Toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.setNavigationOnClickListener { finish() }

        val urlPracticum = getString(R.string.url_practicum)

        binding.tvShareApp.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, urlPracticum)
            startActivity(intent)
        }

        binding.tvWriteToSupport.setOnClickListener {
            val messageHeader = getString(R.string.message_header)
            val messageBody = getString(R.string.message_body)

            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:")
            intent.putExtra(Intent.EXTRA_SUBJECT, messageHeader)
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.email_author)))
            intent.putExtra(Intent.EXTRA_TEXT, messageBody)
            startActivity(intent)
        }

        val urlAgreement = getString(R.string.url_agreement).toUri()

        binding.tvUserAgreement.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, urlAgreement)
            startActivity(intent)
        }

        // работа с темой через интерактор
        binding.smThemeSwitcher.isChecked = themeSettingsInteractor.isDarkTheme()
        binding.smThemeSwitcher.setOnCheckedChangeListener { _, checked ->
            themeSettingsInteractor.setDarkTheme(checked)
            AppCompatDelegate.setDefaultNightMode(
                if (checked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }
    }
}