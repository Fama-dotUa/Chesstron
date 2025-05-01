package com.example.chesstron.presentation.ui

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.times
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chesstron.R
import com.example.chesstron.SoundManager
import com.example.chesstron.data.GameEvent
import com.example.chesstron.data.GameMode
import com.example.chesstron.data.model.PieceColor
import com.example.chesstron.data.model.PieceType
import com.example.chesstron.domain.usecase.ChessCell
import com.example.chesstron.presentation.viewmodel.ChessBoardViewModel
import android.app.Activity
import androidx.compose.ui.platform.LocalContext


@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun ChessBoard(
    gameMode: GameMode = GameMode.SINGLE_DEVICE,
    playerColor: PieceColor = PieceColor.WHITE
) {
    val viewModel: ChessBoardViewModel = viewModel()
    val gameState = viewModel.gameState.value

    val pieces = gameState.pieces
    val selectedPiece = gameState.selectedPiece
    val possibleMoves = gameState.possibleMoves
    val attackablePositions = gameState.attackablePositions
    val context = LocalContext.current
    val rows = 0..7
    val cols = 0..7
    val activity = context as? Activity

    LaunchedEffect(Unit) {
        SoundManager.initialize(context)
    }

    LaunchedEffect(viewModel.lastEvent.value) {
        viewModel.lastEvent.value?.let { event ->
            when (event) {
                GameEvent.MoveMade -> SoundManager.playSound("move")
                GameEvent.CaptureMade -> SoundManager.playSound("capture")
                GameEvent.Check -> SoundManager.playSound("check")
                GameEvent.Checkmate -> SoundManager.playSound("checkmate")
                GameEvent.Stalemate -> SoundManager.playSound("stalemate")
            }
            // ОБОВ'ЯЗКОВО обнуляємо після програвання
            viewModel.lastEvent.value = null
        }
    }
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        val boardSize = min(maxWidth, maxHeight)
        val cellSize = boardSize / 8

        Box(
            modifier = Modifier
                .size(boardSize + 8.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF3E3E3E))
                .shadow(6.dp, RoundedCornerShape(12.dp))
                .padding(1.5.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column {
                    for (row in rows) {
                        Row {
                            for (col in cols) {
                                val (actualRow, actualCol) = adjustCoordinatesForPlayer(row, col, playerColor)

                                val lastMove = viewModel.gameState.value.lastMove

                                ChessCell(
                                    row = row,
                                    col = col,
                                    cellSize = cellSize,
                                    isLastMoveFrom = lastMove?.first == (actualRow to actualCol),
                                    isLastMoveTo = lastMove?.second == (actualRow to actualCol),
                                    isHighlighted = possibleMoves.contains(actualRow to actualCol),
                                    isInCheck = gameState.checkPosition == (actualRow to actualCol),
                                    isCheckmate = gameState.isMate && gameState.checkPosition == (actualRow to actualCol),
                                    onClick = {
                                        if (gameState.gameOver) return@ChessCell

                                        val (actualRow, actualCol) = adjustCoordinatesForPlayer(row, col, playerColor)
                                        val clickedPiece = viewModel.getClickedPiece(actualRow, actualCol) // <<< ВАЖЛИВО!

                                        when {
                                            selectedPiece == null && clickedPiece != null -> {
                                                viewModel.selectPiece(clickedPiece)
                                            }
                                            selectedPiece != null && clickedPiece?.color == selectedPiece.color -> {
                                                viewModel.selectPiece(clickedPiece)
                                            }
                                            selectedPiece != null && possibleMoves.contains(actualRow to actualCol) -> {
                                                viewModel.moveSelectedTo(actualRow, actualCol)
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

                pieces.filter { it.row in 0..7 && it.col in 0..7 }.forEach { piece ->
                    key(piece) {
                        ChessPieceView(
                            piece = piece,
                            cellSize = cellSize,
                            isSelected = selectedPiece == piece,
                            isAttackTarget = attackablePositions.contains(piece.row to piece.col),
                            playerColor = playerColor
                        )
                    }
                }


                gameState.pendingPromotion?.let { pawn ->
                    val promotionWidth = cellSize * 4 + 12.dp
                    val maxOffsetX = boardSize - promotionWidth

                    val promotionOffsetX = (pawn.col * cellSize).coerceIn(0.dp, maxOffsetX)

                    val promotionOffsetY = if (pawn.row == 0) {
                        cellSize
                    } else {
                        -cellSize
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

    if (gameState.isMate) {
        if (gameMode == GameMode.ONLINE) {
            // Повертаємось назад у меню
            LaunchedEffect(Unit) {
                activity?.finish()
            }
        } else {
            AlertDialog(
                onDismissRequest = {},
                title = { Text("Мат!") },
                text = { Text("Гра закінчена.") },
                confirmButton = {
                    Button(onClick = { viewModel.resetGame(gameMode, playerColor) }) {
                        Text("Нова гра")
                    }
                }
            )
        }
    }


    if (gameState.isStalemate) {
        if (gameMode == GameMode.ONLINE) {
            LaunchedEffect(Unit) {
                activity?.finish()
            }
        } else {
            AlertDialog(
                onDismissRequest = {},
                title = { Text("Пат!") },
                text = { Text("Гра закінчилася нічиєю.") },
                confirmButton = {
                    Button(onClick = { viewModel.resetGame(gameMode, playerColor) }) {
                        Text("Нова гра")
                    }
                }
            )
        }
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

fun adjustCoordinatesForPlayer(row: Int, col: Int, playerColor: PieceColor): Pair<Int, Int> {
    return if (playerColor == PieceColor.WHITE) {
        row to col
    } else {
        (7 - row) to (7 - col)
    }
}