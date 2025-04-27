package com.example.chesstron.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.times
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chesstron.R
import com.example.chesstron.data.model.PieceColor
import com.example.chesstron.data.model.PieceType
import com.example.chesstron.domain.usecase.ChessCell
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

        // 🔫 Рамка дошки
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

                // 📆 Клітинки
                Column {
                    for (row in 0 until 8) {
                        Row {
                            for (col in 0 until 8) {
                                ChessCell(
                                    row = row,
                                    col = col,
                                    cellSize = cellSize,
                                    isHighlighted = possibleMoves.contains(row to col),
                                    isInCheck = viewModel.checkPosition.value == row to col,
                                    isCheckmate = viewModel.isMate.value && viewModel.checkPosition.value == row to col,
                                    onClick = {
                                        val clickedPiece = viewModel.getClickedPiece(row, col)

                                        when {
                                            selectedPiece == null && clickedPiece != null -> {
                                                viewModel.selectPiece(clickedPiece)
                                            }
                                            selectedPiece != null && clickedPiece?.color == selectedPiece.color -> {
                                                viewModel.selectPiece(clickedPiece)
                                            }
                                            selectedPiece != null && possibleMoves.contains(row to col) -> {
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


                // 🌟 Промоція над пішаком
                viewModel.pendingPromotion.value?.let { pawn ->
                    val promotionWidth = cellSize * 4 + 12.dp // ширина вікна 4 кнопки + відступи
                    val maxOffsetX = boardSize - promotionWidth

                    val promotionOffsetX = (pawn.col * cellSize).coerceIn(0.dp, maxOffsetX)

                    val promotionOffsetY = if (pawn.row == 0) {
                        cellSize // віконце під пішаком
                    } else {
                        -cellSize // віконце над пішаком
                    }
                    Box(
                        modifier = Modifier
                            .absoluteOffset(
                                x = promotionOffsetX,
                                y = pawn.row * cellSize + promotionOffsetY
                            )
                            .background(Color(0xFFCBA697), shape = RoundedCornerShape(8.dp))
                            .padding(4.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            PromotionButton(PieceType.QUEEN, pawn.color) { viewModel.promotePawn(PieceType.QUEEN) }
                            PromotionButton(PieceType.ROOK, pawn.color) { viewModel.promotePawn(PieceType.ROOK) }
                            PromotionButton(PieceType.BISHOP, pawn.color) { viewModel.promotePawn(PieceType.BISHOP) }
                            PromotionButton(PieceType.KNIGHT, pawn.color) { viewModel.promotePawn(PieceType.KNIGHT) }
                        }
                    }
                }

            }
        }
    }




    if (viewModel.isMate.value) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Мат!") },
            text = { Text("Гра закінчена.") },
            confirmButton = {
                Button(onClick = { viewModel.resetGame() }) {
                    Text("Нова гра")
                }
            }
        )
    }

    if (viewModel.isStalemate.value) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Пат!") },
            text = { Text("Гра закінчилася нічиєю.") },
            confirmButton = {
                Button(onClick = { viewModel.resetGame() }) {
                    Text("Нова гра")
                }
            }
        )
    }
}
@Composable
fun PromotionButton(type: PieceType, color: PieceColor, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(48.dp),
        contentPadding = PaddingValues(4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFAB9970) // тут обираєш колір фону!
        )
    ) {
        Image(
            painter = painterResource(id = getDrawableForPromotion(color, type)),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
    }
}

fun getDrawableForPromotion(color: PieceColor, type: PieceType): Int {
    return when (color) {
        PieceColor.WHITE -> when (type) {
            PieceType.QUEEN -> R.drawable.white_queen
            PieceType.ROOK -> R.drawable.white_rook
            PieceType.BISHOP -> R.drawable.white_bishop
            PieceType.KNIGHT -> R.drawable.white_knight
            else -> R.drawable.white_queen // страховка
        }
        PieceColor.BLACK -> when (type) {
            PieceType.QUEEN -> R.drawable.black_queen
            PieceType.ROOK -> R.drawable.black_rook
            PieceType.BISHOP -> R.drawable.black_bishop
            PieceType.KNIGHT -> R.drawable.black_knight
            else -> R.drawable.black_queen
        }
    }
}

