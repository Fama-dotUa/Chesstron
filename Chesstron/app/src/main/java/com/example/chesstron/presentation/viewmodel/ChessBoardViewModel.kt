package com.example.chesstron.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.chesstron.data.model.ChessPiece
import com.example.chesstron.data.model.ChessRules
import com.example.chesstron.data.model.GameState
import com.example.chesstron.data.model.PieceColor
import com.example.chesstron.data.model.PieceType
import com.example.chesstron.domain.usecase.initializePieces

class ChessBoardViewModel : ViewModel() {

    var gameState = mutableStateOf(GameState())
        private set

    init {
        resetGame()
    }

    fun selectPiece(piece: ChessPiece) {
        if (piece.color != gameState.value.currentTurn || gameState.value.gameOver) return

        val moves = ChessRules.generateMoves(piece, gameState.value.pieces, gameState.value.enPassantTarget)

        val filteredMoves = if (gameState.value.checkPosition != null) {
            moves.filter { move ->
                isMoveSafe(piece, move.first, move.second)
            }
        } else moves

        gameState.value = gameState.value.copy(
            selectedPiece = piece,
            possibleMoves = filteredMoves,
            attackablePositions = filteredMoves.filter { pos ->
                gameState.value.pieces.any { it.row == pos.first && it.col == pos.second && it.color != piece.color }
            }
        )
    }

    fun deselectPiece() {
        gameState.value = gameState.value.copy(
            selectedPiece = null,
            possibleMoves = emptyList(),
            attackablePositions = emptyList()
        )
    }

    fun moveSelectedTo(row: Int, col: Int) {
        val piece = gameState.value.selectedPiece ?: return
        if (!gameState.value.possibleMoves.contains(row to col)) return

        val startRow = piece.row
        val startCol = piece.col
        // Рокіровка
        if (piece.type == PieceType.KING && kotlin.math.abs(col - startCol) == 2) {
            val rookCol = if (col > startCol) 7 else 0
            val newRookCol = if (col > startCol) 5 else 3
            val rook = gameState.value.pieces.find { it.row == startRow && it.col == rookCol && it.color == piece.color }
            rook?.let {
                it.col = newRookCol
                it.hasMoved = true
            }
        }

        val enPassantTarget = gameState.value.enPassantTarget

        // Взяття на проході
        if (piece.type == PieceType.PAWN && enPassantTarget == row to col) {
            val capturedRow = if (piece.color == PieceColor.WHITE) row + 1 else row - 1
            gameState.value.pieces.find { it.row == capturedRow && it.col == col && it.color != piece.color }?.apply {
                this.row = -1
                this.col = -1
            }
        }
        // Звичайне взяття
        else {
            gameState.value.pieces.find { it.row == row && it.col == col && it.color != piece.color }?.apply {
                this.row = -1
                this.col = -1
            }
        }

        // Переміщення фігури
        piece.row = row
        piece.col = col
        piece.hasMoved = true

        // Промоція пішака
        val promotionRow = if (piece.color == PieceColor.WHITE) 0 else 7
        if (piece.type == PieceType.PAWN && piece.row == promotionRow) {
            gameState.value = gameState.value.copy(
                pendingPromotion = piece,
                selectedPiece = null,
                possibleMoves = emptyList(),
                attackablePositions = emptyList()
            )
            return
        }

        // Оновлення en passant
        val newEnPassantTarget = if (piece.type == PieceType.PAWN && kotlin.math.abs(row - startRow) == 2) {
            val dir = if (piece.color == PieceColor.WHITE) -1 else 1
            Pair(startRow + dir, startCol)
        } else null

        // Оновлюємо стан гри
        gameState.value = gameState.value.copy(
            selectedPiece = null,
            possibleMoves = emptyList(),
            attackablePositions = emptyList(),
            enPassantTarget = newEnPassantTarget,
            lastMove = (startRow to startCol) to (row to col)
        )

        updateGameState()

        gameState.value = gameState.value.copy(
            currentTurn = gameState.value.currentTurn.opposite()
        )
    }



    fun promotePawn(newType: PieceType) {
        val pawn = gameState.value.pendingPromotion ?: return
        val newPieces = gameState.value.pieces.toMutableList()
        pawn.type = newType
        pawn.hasMoved = true


        gameState.value = gameState.value.copy(
            pieces = newPieces,
            pendingPromotion = null
        )

        updateGameState()
        gameState.value = gameState.value.copy(
            currentTurn = gameState.value.currentTurn.opposite()
        )
    }

    private fun updateGameState() {
        val opponentColor = gameState.value.currentTurn.opposite()
        val checkPos = ChessRules.getCheckPosition(opponentColor, gameState.value.pieces, gameState.value.enPassantTarget)
        val mate = checkPos != null && ChessRules.isCheckmate(opponentColor, gameState.value.pieces, gameState.value.enPassantTarget)
        val stalemate = !mate && ChessRules.isStalemate(opponentColor, gameState.value.pieces, gameState.value.enPassantTarget)

        gameState.value = gameState.value.copy(
            checkPosition = checkPos,
            isMate = mate,
            isStalemate = stalemate,
            gameOver = mate || stalemate
        )
    }

    fun resetGame() {
        gameState.value = GameState(
            pieces = initializePieces(),
            currentTurn = PieceColor.WHITE
        )
    }

    fun getClickedPiece(row: Int, col: Int): ChessPiece? =
        gameState.value.pieces.find { it.row == row && it.col == col }

    private fun isMoveSafe(piece: ChessPiece, toRow: Int, toCol: Int): Boolean {
        val snapshot = gameState.value.pieces.map { it.copy() }.toMutableList()
        val moving = snapshot.find { it.row == piece.row && it.col == piece.col && it.color == piece.color } ?: return false

        snapshot.removeAll { it.row == toRow && it.col == toCol && it.color != piece.color }
        moving.row = toRow
        moving.col = toCol

        val king = snapshot.find { it.type == PieceType.KING && it.color == piece.color } ?: return false

        return snapshot.none { opponent ->
            opponent.color != piece.color && ChessRules.isMoveValid(opponent, king.row, king.col, snapshot, gameState.value.enPassantTarget)
        }
    }
}

fun PieceColor.opposite(): PieceColor {
    return when (this) {
        PieceColor.WHITE -> PieceColor.BLACK
        PieceColor.BLACK -> PieceColor.WHITE
    }
}