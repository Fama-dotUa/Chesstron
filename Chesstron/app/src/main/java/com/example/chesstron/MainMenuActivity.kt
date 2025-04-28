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

class MainMenuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier.width(200.dp)
                ) {
                    Text("Нова гра")
                }
                Button(
                    onClick = {
                        // Тут поки що можна зробити тост "Функція в розробці"
                    },
                    modifier = Modifier.width(200.dp)
                ) {
                    Text("Продовжити")
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
                            modifier = Modifier.width(200.dp)
                        ) {
                            Text("Гра проти комп'ютера")
                        }

                    }
                },
                confirmButton = {},
                dismissButton = {}
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