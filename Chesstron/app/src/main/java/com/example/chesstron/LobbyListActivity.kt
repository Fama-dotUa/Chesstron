package com.example.chesstron

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.chesstron.data.model.GameSession
import com.example.chesstron.data.model.PieceColor
import com.example.chesstron.presentation.viewmodel.ChessBoardViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.FirebaseFirestore

class LobbyListActivity : ComponentActivity() {

    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var listView: ListView
    private lateinit var progressBar: ProgressBar
    private val lobbies = mutableListOf<GameSession>()
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        progressBar = ProgressBar(this)
        listView = ListView(this)

        layout.addView(progressBar)
        layout.addView(listView)
        setContentView(layout)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf())
        listView.adapter = adapter

        loadLobbies()

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedLobby = lobbies[position]
            joinLobby(selectedLobby)
        }
    }

    private fun loadLobbies() {
        progressBar.visibility = ProgressBar.VISIBLE
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val result = firestore.collection("games")
                    .whereEqualTo("status", "waiting")
                    .get()
                    .await()

                lobbies.clear()
                lobbies.addAll(result.documents.mapNotNull { it.toObject(GameSession::class.java) })

                val names = lobbies.map { it.name.ifEmpty { it.gameId } }

                runOnUiThread {
                    adapter.clear()
                    adapter.addAll(names)
                    progressBar.visibility = ProgressBar.GONE
                }

            } catch (e: Exception) {
                Log.e("LOBBY", "Не вдалося завантажити лобі: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this@LobbyListActivity, "Помилка завантаження лобі", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun joinLobby(session: GameSession) {
        val intent = Intent(this, ChessBoardActivity::class.java).apply {
            putExtra("game_mode", "ONLINE")
            putExtra("player_color", PieceColor.BLACK.name)
            putExtra("online_action", "join")
            putExtra("game_id", session.gameId)
        }
        startActivity(intent)
        finish()
    }
}
