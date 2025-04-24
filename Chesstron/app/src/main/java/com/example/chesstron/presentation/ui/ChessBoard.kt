package com.example.chesstron.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp

@Composable
fun ChessBoard() {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        val boardSize = min(maxWidth, maxHeight)
        val cellSize = boardSize / 8

        val lightSquare = Color(0xFFF0D9B5)
        val darkSquare = Color(0xFFB58863)
        val coordColor = Color(0xFF1A1A1A) // темно-сірий

        // Рамка + дошка
        Box(
            modifier = Modifier
                .size(boardSize + 8.dp) // трохи більша рамка
                .background(Color(0xFF795B3D), shape = RoundedCornerShape(8.dp)) // рамка
                .padding(4.dp)
        ) {
            Column {
                for (row in 0 until 8) {
                    Row {
                        for (col in 0 until 8) {
                            val isLight = (row + col) % 2 == 0
                            val cellColor = if (isLight) lightSquare else darkSquare

                            Box(
                                modifier = Modifier
                                    .size(cellSize)
                                    .background(cellColor)
                            ) {
                                // Цифра (зліва)
                                if (col == 0) {
                                    Text(
                                        text = "${8 - row}",
                                        color = coordColor,
                                        fontSize = 14.sp,
                                        modifier = Modifier
                                            .align(Alignment.TopStart)
                                            .padding(6.dp)
                                    )
                                }

                                // Літера (внизу)
                                if (row == 7) {
                                    Text(
                                        text = ('a' + col).toString(),
                                        color = coordColor,
                                        fontSize = 14.sp,
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .padding(6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
