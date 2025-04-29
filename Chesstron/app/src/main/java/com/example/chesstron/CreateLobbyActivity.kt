package com.example.chesstron

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.ComponentActivity
import com.example.chesstron.data.model.PieceColor
import com.example.chesstron.presentation.viewmodel.ChessBoardViewModel

class CreateLobbyActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 80, 40, 40)
        }

        val nameEdit = EditText(this).apply {
            hint = "Назва лобі"
        }

        val passwordEdit = EditText(this).apply {
            hint = "Пароль (необов'язково)"
        }

        val colorOptions = listOf("Random", "White", "Black")
        val spinner = Spinner(this).apply {
            adapter = ArrayAdapter(this@CreateLobbyActivity, android.R.layout.simple_spinner_dropdown_item, colorOptions)
        }

        val createButton = Button(this).apply {
            text = "Створити гру"
        }

        layout.addView(nameEdit)
        layout.addView(passwordEdit)
        layout.addView(spinner)
        layout.addView(createButton)

        setContentView(layout)

        createButton.setOnClickListener {
            val name = nameEdit.text.toString().trim()
            val password = passwordEdit.text.toString().takeIf { it.isNotBlank() }
            if (name.isBlank()) {
                Toast.makeText(this, "Введіть назву лобі", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selected = spinner.selectedItem.toString()
            val color = when (selected) {
                "White" -> PieceColor.WHITE
                "Black" -> PieceColor.BLACK
                else -> if ((0..1).random() == 0) PieceColor.WHITE else PieceColor.BLACK
            }

            // Після натискання "Створити гру"
            val intent = Intent(this, ChessBoardActivity::class.java).apply {
                putExtra("game_mode", "ONLINE")
                putExtra("player_color", color.name)
                putExtra("online_action", "create")
                putExtra("lobby_name", name)
                putExtra("lobby_password", password)
            }
            startActivity(intent)
            finish()

        }
    }
}
