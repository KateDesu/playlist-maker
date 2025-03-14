package com.practicum.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/

        // реализация клика через анонимный класс
        val btnSearch=findViewById<Button>(R.id.btn_search)
        
        val btnSearchClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "Нажатие на Поиск", Toast.LENGTH_SHORT).show()
            }
        }

        btnSearch.setOnClickListener(btnSearchClickListener)

        // реализация клика через лямбда-выражение
        val btnLibrary = findViewById<Button>(R.id.btn_library)

        btnLibrary.setOnClickListener {
            Toast.makeText(this@MainActivity, "Нажатие на Медиатеку", Toast.LENGTH_SHORT).show()
        }

        val btnSettings = findViewById<Button>(R.id.btn_settings)

        btnSettings.setOnClickListener {
            Toast.makeText(this@MainActivity, "Нажатие на Настройки", Toast.LENGTH_SHORT).show()
        }
    }
}