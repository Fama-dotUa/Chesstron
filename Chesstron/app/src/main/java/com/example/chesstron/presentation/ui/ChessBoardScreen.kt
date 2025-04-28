package com.example.chesstron.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chesstron.data.GameMode
import com.example.chesstron.data.model.PieceColor
import com.example.chesstron.presentation.viewmodel.ChessBoardViewModel

@Composable
fun ChessBoardScreen(
    gameMode: GameMode,
    playerColor: PieceColor
) {
    val viewModel: ChessBoardViewModel = viewModel()

    LaunchedEffect(Unit) {
        viewModel.resetGame(gameMode, playerColor)
    }
    ChessBoard(gameMode = gameMode, playerColor = playerColor)
}
