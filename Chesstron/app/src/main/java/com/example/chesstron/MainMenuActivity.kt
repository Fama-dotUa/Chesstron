package com.example.chesstron

import android.os.Bundle
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.chesstron.ui.theme.ChesstronTheme
import com.example.chesstron.data.model.PieceColor
import com.example.chesstron.data.GameMode
import com.google.firebase.FirebaseApp

class MainMenuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this) // 🔥 ініціалізує Firebase
        setContent {
            ChesstronTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainMenuContent()
                }
            }
        }
    }

    @Composable
    fun MainMenuContent() {
        var showDialog by remember { mutableStateOf(false) }
        var showColorChoiceDialog by remember { mutableStateOf(false) }
        var showGameIdInputDialog by remember { mutableStateOf(false) }
        var gameIdInput by remember { mutableStateOf("") }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        // Тут поки що можна зробити тост "Функція в розробці"
                    },
                    modifier = Modifier.width(200.dp)
                ) {
                    Text("Продовжити")
                }
                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier.width(200.dp)
                ) {
                    Text("Грати локально")
                }

                Button(
                    onClick = {
                        val intent = Intent(this@MainMenuActivity, CreateLobbyActivity::class.java).apply {
                            putExtra("game_mode", GameMode.ONLINE.name)
                            putExtra("online_action", "create")
                        }
                        startActivity(intent)
                    },
                    modifier = Modifier.width(200.dp)
                ) {
                    Text("Створити онлайн-гру")
                }

                Button(onClick = {
                    val intent = Intent(this@MainMenuActivity, LobbyListActivity::class.java)
                    startActivity(intent)
                }) {
                    Text("Приєднатися до гри")
                }

            }
        }

        if (showColorChoiceDialog) {
            AlertDialog(
                onDismissRequest = { showColorChoiceDialog = false },
                title = { Text("Виберіть фігуру") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                showColorChoiceDialog = false
                                startChessGame(PieceColor.WHITE)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Білими")
                        }
                        Button(
                            onClick = {
                                showColorChoiceDialog = false
                                startChessGame(PieceColor.BLACK)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Чорними")
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {}
            )
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Виберіть режим гри") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                showDialog = false
                                startActivity(Intent(this@MainMenuActivity, ChessBoardActivity::class.java))
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Гра на одному пристрої")
                        }
                        Button(
                            onClick = { showColorChoiceDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Гра проти комп'ютера")
                        }

                    }
                },
                confirmButton = {},
                dismissButton = {}
            )
        }

        if (showGameIdInputDialog) {
            AlertDialog(
                onDismissRequest = { showGameIdInputDialog = false },
                title = { Text("Введіть ID гри") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = gameIdInput,
                            onValueChange = { gameIdInput = it },
                            label = { Text("Game ID") }
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        showGameIdInputDialog = false
                        val intent = Intent(this@MainMenuActivity, ChessBoardActivity::class.java).apply {
                            putExtra("game_mode", GameMode.ONLINE.name)
                            putExtra("player_color", PieceColor.BLACK.name)
                            putExtra("online_action", "join")
                            putExtra("game_id", gameIdInput)
                        }
                        startActivity(intent)
                    }) {
                        Text("Приєднатися")
                    }
                },
                dismissButton = {
                    Button(onClick = { showGameIdInputDialog = false }) {
                        Text("Скасувати")
                    }
                }
            )
        }

    }


    private fun startChessGame(playerColor: PieceColor) {
        val intent = Intent(this, ChessBoardActivity::class.java).apply {
            putExtra("game_mode", GameMode.VS_COMPUTER.name)
            putExtra("player_color", playerColor.name)
        }
        startActivity(intent)
    }

}