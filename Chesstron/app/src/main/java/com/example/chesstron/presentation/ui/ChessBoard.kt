package com.example.chesstron.presentation.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.times
import com.example.chesstron.data.model.ChessPiece
import com.example.chesstron.domain.usecase.ChessCell
import com.example.chesstron.domain.usecase.initializePieces
import com.example.chesstron.domain.usecase.isMoveValid

@Composable
fun ChessBoard() {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        var selectedPiece by remember { mutableStateOf<ChessPiece?>(null) }
        val pieces = remember { mutableStateListOf<ChessPiece>() }
        val possibleMoves = remember { mutableStateListOf<Pair<Int, Int>>() }
        val attackablePositions = remember { mutableStateListOf<Pair<Int, Int>>() }
        val boardSize = min(maxWidth, maxHeight)
        val cellSize = boardSize / 8

        // 🟫 Рамка дошки
        Box(
            modifier = Modifier
                .size(boardSize + 8.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF3E3E3E))
                .shadow(6.dp, RoundedCornerShape(12.dp))
                .padding(1.5.dp)
        ) {
            // ♟️ Внутрішня дошка
            Box(modifier = Modifier.fillMaxSize()) {

                // 📦 Клітинки
                Column {
                    for (row in 0 until 8) {
                        Row {
                            for (col in 0 until 8) {
                                ChessCell(
                                    row = row,
                                    col = col,
                                    cellSize = cellSize,
                                    isHighlighted = possibleMoves.contains(row to col),
                                    onClick = {
                                        val clickedPiece = pieces.find { it.row == row && it.col == col }

                                        when {
                                            selectedPiece == null && clickedPiece != null -> {
                                                // Вибір фігури
                                                selectedPiece = clickedPiece
                                                possibleMoves.clear()
                                                possibleMoves.addAll(
                                                    (0 until 8).flatMap { r ->
                                                        (0 until 8).mapNotNull { c ->
                                                            if (isMoveValid(clickedPiece, r, c, pieces)) Pair(r, c) else null
                                                        }
                                                    }
                                                )
                                            }

                                            selectedPiece != null && clickedPiece?.color == selectedPiece!!.color -> {
                                                // Клік по своїй фігурі → переобираємо
                                                selectedPiece = clickedPiece
                                                possibleMoves.clear()
                                                possibleMoves.addAll(
                                                    (0 until 8).flatMap { r ->
                                                        (0 until 8).mapNotNull { c ->
                                                            if (isMoveValid(clickedPiece, r, c, pieces)) Pair(r, c) else null
                                                        }
                                                    }
                                                )
                                            }

                                            selectedPiece != null && isMoveValid(selectedPiece!!, row, col, pieces) -> {
                                                // Переміщення або биття
                                                pieces.removeAll { it.row == row && it.col == col && it.color != selectedPiece!!.color }
                                                selectedPiece!!.row = row
                                                selectedPiece!!.col = col
                                                selectedPiece = null
                                                possibleMoves.clear()
                                            }

                                            else -> {
                                                // Невірна дія → просто скидуємо
                                                selectedPiece = null
                                                possibleMoves.clear()
                                            }
                                        }
                                    }

                                )
                            }
                        }
                    }
                }

                // ♟️ Фігури поверх
                pieces.forEach { piece ->

                    ChessPieceView(
                        piece = piece,
                        cellSize = cellSize,
                        isSelected = selectedPiece == piece,

                    )

                }
            }
        }

        // 📦 Стартові фігури
        LaunchedEffect(Unit) {
            if (pieces.isEmpty()) {
                pieces.addAll(initializePieces())
            }
        }
    }
}


