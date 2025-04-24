package com.example.chesstron.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.example.chesstron.domain.usecase.ChessCell
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chesstron.domain.usecase.isMoveValid
import com.example.chesstron.viewmodel.ChessBoardViewModel

@Composable
fun ChessBoard() {
    val viewModel: ChessBoardViewModel = viewModel()

    val pieces = viewModel.pieces
    val selectedPiece = viewModel.selectedPiece.value
    val possibleMoves = viewModel.possibleMoves
    val attackablePositions = viewModel.attackablePositions

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
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
                                        val clickedPiece = viewModel.getClickedPiece(row, col)

                                        when {
                                            selectedPiece == null && clickedPiece != null -> {
                                                viewModel.selectPiece(clickedPiece)
                                            }

                                            selectedPiece != null && clickedPiece?.color == selectedPiece.color -> {
                                                viewModel.selectPiece(clickedPiece)
                                            }

                                            selectedPiece != null &&
                                                    isMoveValid(
                                                        selectedPiece, row, col, pieces
                                                    ) -> {
                                                viewModel.moveSelectedTo(row, col)
                                            }

                                            else -> {
                                                viewModel.deselectPiece()
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
                        isAttackTarget = attackablePositions.contains(piece.row to piece.col)
                    )
                }
            }
        }
    }
}


