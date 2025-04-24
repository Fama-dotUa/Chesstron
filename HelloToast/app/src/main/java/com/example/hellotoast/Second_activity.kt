package com.example.hellotoast

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Second_activity : AppCompatActivity() {

    private lateinit var countEditText: EditText
    private lateinit var colorEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_second)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        countEditText = findViewById(R.id.edit_count)
        colorEditText = findViewById(R.id.edit_color)

        val count = intent.getIntExtra("count", 0)
        val color = intent.getIntExtra("color", Color.LTGRAY)

        countEditText.setText(count.toString())
        colorEditText.setText(String.format("#%06X", 0xFFFFFF and color))
    }

    fun saveSettings(view: View) {
        val resultIntent = Intent()
        val count = countEditText.text.toString().toIntOrNull() ?: 0
        val colorStr = colorEditText.text.toString()

        try {
            val color = Color.parseColor(colorStr)
            resultIntent.putExtra("count", count)
            resultIntent.putExtra("color", color)
            setResult(RESULT_OK, resultIntent)
            finish()
        } catch (e: IllegalArgumentException) {
            Toast.makeText(this, "Неверный формат цвета", Toast.LENGTH_SHORT).show()
        }
    }

    fun resetSettings(view: View) {
        countEditText.setText("0")
        colorEditText.setText("#D3D3D3") // LTGRAY
    }
}