package com.example.chesstron

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.chesstron.data.model.GameSession
import com.example.chesstron.data.model.PieceColor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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

        val joiningColor = when {
            session.playerWhiteId.isEmpty() -> PieceColor.WHITE
            session.playerBlackId.isEmpty() -> PieceColor.BLACK
            else -> null
        }

        if (joiningColor == null) {
            Toast.makeText(this, "Лобі вже заповнене", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(this, ChessBoardActivity::class.java).apply {
            putExtra("game_mode", "ONLINE")
            putExtra("player_color", joiningColor.name)
            putExtra("online_action", "join")
            putExtra("game_id", session.gameId)
        }
        startActivity(intent)
        finish()
    }
}
