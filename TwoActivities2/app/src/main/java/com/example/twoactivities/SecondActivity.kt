package com.example.twoactivities

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class SecondActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_second)

        title = getString(R.string.activity2_name)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity2_name)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        // отримати Intent, який активує дане Activity
        val intent = intent

        // отримати рядок з intent по ключу EXTRA_MESSAGE
        val message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE)

        // використати findViewById() щоб отримати посилання на textView з розмітки
        val textView = findViewById<TextView>(R.id.text_message)

        // встановити текст для textView
        textView.text = message

    }
}