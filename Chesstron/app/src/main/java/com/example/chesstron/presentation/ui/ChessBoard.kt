package com.example.chesstron.presentation.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.min
import com.example.chesstron.data.model.ChessPiece
import com.example.chesstron.domain.usecase.initializePieces

@Composable
fun ChessBoard() {

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center // 👈 Центруємо дошку
    ) {
        var selectedPiece by remember { mutableStateOf<ChessPiece?>(null) }

        val boardSize = min(maxWidth, maxHeight)
        val cellSize = boardSize / 8

        val modifier = Modifier
            .size(boardSize + 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF3E3E3E))
            .padding(4.dp)

        Box(modifier = modifier) {
            // 1. Клітинки
            Column {
                for (row in 0 until 8) {
                    Row {
                        for (col in 0 until 8) {
                            ChessCell(
                                row = row,
                                col = col,
                                cellSize = cellSize,
                                onClick = {
                                    if (selectedPiece != null) {
                                        selectedPiece!!.row = row
                                        selectedPiece!!.col = col
                                        selectedPiece = null
                                    }
                                }
                            )
                        }
                    }
                }
            }
            val pieces = remember { mutableStateListOf<ChessPiece>() }

            LaunchedEffect(Unit) {
                if (pieces.isEmpty()) {
                    pieces.addAll(initializePieces())
                }
            }
            pieces.forEach { piece ->
                ChessPieceView(
                    piece = piece,
                    cellSize = cellSize,
                    isSelected = selectedPiece == piece,
                    onClick = {
                        selectedPiece = if (selectedPiece == piece) null else piece
                    }
                )
            }
        }
    }
}

@Composable
fun ChessCell(
    row: Int,
    col: Int,
    cellSize: Dp,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }

    val isLight = (row + col) % 2 == 0
    val baseColor = if (isLight) Color(0xFFF0D9B5) else Color(0xFFB58863)
    val pressedColor = if (isLight) Color(0xFFE8C785) else Color(0xFF9C6C48)

    val animatedColor by animateColorAsState(
        targetValue = if (isPressed) pressedColor else baseColor,
        animationSpec = tween(durationMillis = 400), // 👈 повільніше і плавніше
        label = "CellColor"
    )
    val coordColor = Color(0xFF1A1A1A)

    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(300L)
            isPressed = false
        }
    }

    Box(
        modifier = Modifier
            .size(cellSize)
            .background(animatedColor)
            .clickable {
                isPressed = true
            }
            .clickable {
                isPressed = true
                onClick()
            }

    ) {
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

