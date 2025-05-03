package com.example.chesstron

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chesstron.data.GameMode
import com.example.chesstron.data.model.PieceColor
import com.example.chesstron.presentation.ui.ChessBoardScreen
import com.example.chesstron.presentation.viewmodel.ChessBoardViewModel
import com.example.chesstron.ui.theme.ChesstronTheme

class ChessBoardActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val gameMode = intent?.getStringExtra("game_mode")?.let { GameMode.valueOf(it) } ?: GameMode.SINGLE_DEVICE
        val playerColor = intent?.getStringExtra("player_color")?.let { PieceColor.valueOf(it) } ?: PieceColor.WHITE
        val action = intent?.getStringExtra("online_action")
        val lobbyName = intent?.getStringExtra("lobby_name") ?: ""
        val lobbyPassword = intent?.getStringExtra("lobby_password")
        val gameId = intent?.getStringExtra("game_id")

        setContent {
            val viewModel: ChessBoardViewModel = viewModel()

            if (gameMode != GameMode.ONLINE && !viewModel.hasGameBeenInitialized) {
                viewModel.forceResetGame(gameMode, playerColor)
            }


            if (gameMode == GameMode.ONLINE) {
                when (action) {
                    "create" -> {
                        viewModel.createLobby(lobbyName, lobbyPassword, playerColor)
                    }
                    "join" -> {
                        viewModel.joinOnlineGame(gameId!!, playerColor)
                    }
                }
            }

            ChesstronTheme {
                Scaffold(
                    topBar = {
                        if (gameMode != GameMode.ONLINE) {
                            TopAppBar(
                                title = { Text("Chesstron") },
                                actions = {
                                    Button(
                                        onClick = { viewModel.undoMove() }
                                    ) {
                                        Text("Undo")
                                    }
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
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
}
