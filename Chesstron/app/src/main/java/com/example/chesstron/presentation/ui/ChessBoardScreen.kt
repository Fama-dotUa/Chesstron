package com.example.chesstron.presentation.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chesstron.data.GameMode
import com.example.chesstron.data.model.PieceColor
import com.example.chesstron.presentation.viewmodel.ChessBoardViewModel

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun ChessBoardScreen(
    gameMode: GameMode,
    playerColor: PieceColor
) {

    ChessBoard(gameMode = gameMode, playerColor = playerColor)
}
