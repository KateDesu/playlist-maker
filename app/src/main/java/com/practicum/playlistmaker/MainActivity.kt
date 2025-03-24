package com.practicum.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // реализация клика через анонимный класс
        val searchButton=findViewById<Button>(R.id.btn_search)
        
        val searchButtonClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                //Toast.makeText(this@MainActivity, "Нажатие на Поиск", Toast.LENGTH_SHORT).show()
                val searchIntent = Intent(this@MainActivity, SearchActivity::class.java)
                startActivity(searchIntent)
            }
        }

        searchButton.setOnClickListener(searchButtonClickListener)


        // реализация клика через лямбда-выражение
        val libraryButton = findViewById<Button>(R.id.btn_library)

        libraryButton.setOnClickListener {
            //Toast.makeText(this@MainActivity, "Нажатие на Медиатеку", Toast.LENGTH_SHORT).show()
            val libraryIntent = Intent(this, LibraryActivity::class.java)
            startActivity(libraryIntent)

        }

        val settingsButton = findViewById<Button>(R.id.btn_settings)

        settingsButton.setOnClickListener {
            //Toast.makeText(this@MainActivity, "Нажатие на Настройки", Toast.LENGTH_SHORT).show()
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }
    }
}