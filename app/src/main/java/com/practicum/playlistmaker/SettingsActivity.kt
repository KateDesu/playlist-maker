package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val shareTextView = findViewById<TextView>(R.id.text_view_share_app)
        val urlPracticum = Uri.parse(getString(R.string.url_practicum))

        shareTextView.setOnClickListener{
            val intent=Intent(Intent.ACTION_SEND)
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
        val urlAgreement = Uri.parse(getString(R.string.url_agreement))

        userAgreementTextView.setOnClickListener{
            val intent=Intent(Intent.ACTION_VIEW, urlAgreement)
            startActivity(intent)
        }
    }
}