package com.example.chesstron

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.chesstron.data.GameMode
import com.example.chesstron.data.model.PieceColor
import com.example.chesstron.presentation.ui.ChessBoardScreen
import com.example.chesstron.ui.theme.ChesstronTheme

class ChessBoardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val gameMode = intent?.getStringExtra("game_mode")?.let { GameMode.valueOf(it) } ?: GameMode.SINGLE_DEVICE
        val playerColor = intent?.getStringExtra("player_color")?.let { PieceColor.valueOf(it) } ?: PieceColor.WHITE

        setContent {
            ChesstronTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChessBoardScreen(
                        gameMode = gameMode,
                        playerColor = playerColor
                    )
                }
            }
        }
    }
}