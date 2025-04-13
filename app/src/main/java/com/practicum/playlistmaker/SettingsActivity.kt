package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val shareTextView = findViewById<TextView>(R.id.text_view_share_app)
        val urlPracticum = getString(R.string.url_practicum).toUri()
            //Uri.parse(getString(R.string.url_practicum))

        shareTextView.setOnClickListener{
            val intent=Intent(Intent.ACTION_SEND)
            intent.type="text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, urlPracticum)
            startActivity(intent)
        }

        val writeToSupportTextView = findViewById<TextView>(R.id.text_view_write_to_support)

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

        val userAgreementTextView = findViewById<TextView>(R.id.text_view_user_agreement)
        val urlAgreement = getString(R.string.url_agreement).toUri()
            //Uri.parse(getString(R.string.url_agreement))

        userAgreementTextView.setOnClickListener{
            val intent=Intent(Intent.ACTION_VIEW, urlAgreement)
            startActivity(intent)
        }
    }
}