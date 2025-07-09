package com.practicum.playlistmaker.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.PLAYLISTMAKER_PREFERENCES
import com.practicum.playlistmaker.R

class SettingsActivity : AppCompatActivity() {
    private lateinit var themeSwitch: SwitchMaterial

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Включаем кнопку "Назад" в Toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        val shareTextView = findViewById<TextView>(R.id.tvShareApp)
        val urlPracticum = getString(R.string.url_practicum)

        shareTextView.setOnClickListener{
            val intent=Intent(Intent.ACTION_SEND)
            intent.type="text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, urlPracticum)
            startActivity(intent)
        }

        val writeToSupportTextView = findViewById<TextView>(R.id.tvWriteToSupport)

        writeToSupportTextView.setOnClickListener {
            val messageHeader = getString(R.string.message_header)
            val messageBody=getString(R.string.message_body)

            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:")
            intent.putExtra(Intent.EXTRA_SUBJECT, messageHeader)
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.email_author)))
            intent.putExtra(Intent.EXTRA_TEXT, messageBody)
            startActivity(intent)
        }

        val userAgreementTextView = findViewById<TextView>(R.id.tvUserAgreement)
        val urlAgreement = getString(R.string.url_agreement).toUri()

        userAgreementTextView.setOnClickListener{
            val intent=Intent(Intent.ACTION_VIEW, urlAgreement)
            startActivity(intent)
        }

        // переключение на светлую / темную тему
        themeSwitch = findViewById(R.id.smThemeSwitcher)

        themeSwitch.isChecked = (application as App).darkTheme

        themeSwitch.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
            val sharedPrefs = getSharedPreferences(PLAYLISTMAKER_PREFERENCES, MODE_PRIVATE)
        }
    }
}