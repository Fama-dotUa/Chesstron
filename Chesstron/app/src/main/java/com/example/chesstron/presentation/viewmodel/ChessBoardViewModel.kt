package com.example.chesstron.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.chesstron.data.model.ChessPiece
import com.example.chesstron.data.model.PieceColor
import com.example.chesstron.data.model.PieceType
import com.example.chesstron.domain.usecase.initializePieces
import com.example.chesstron.domain.usecase.isMoveValid

class ChessBoardViewModel : ViewModel() {

    var pieces = mutableStateListOf<ChessPiece>()
        private set

    var selectedPiece = mutableStateOf<ChessPiece?>(null)
        private set

    var possibleMoves = mutableStateListOf<Pair<Int, Int>>()
        private set

    var attackablePositions = mutableStateListOf<Pair<Int, Int>>()
        private set

    var currentTurn = mutableStateOf(PieceColor.WHITE)
        private set

    var isStalemate = mutableStateOf(false)
        private set

    var pendingPromotion = mutableStateOf<ChessPiece?>(null)
        private set

    var enPassantTarget = mutableStateOf<Pair<Int, Int>?>(null)

    init {
        if (pieces.isEmpty()) {
            pieces.addAll(initializePieces())
        }
    }

    var checkPosition = mutableStateOf<Pair<Int, Int>?>(null)
    var isMate = mutableStateOf(false)

    fun selectPiece(piece: ChessPiece) {
        if (piece.color != currentTurn.value) return

        selectedPiece.value = piece
        possibleMoves.clear()
        attackablePositions.clear()

        val moves = generateMoves(piece)
        possibleMoves.addAll(moves)

        attackablePositions.addAll(
            moves.filter { pos ->
                val target = pieces.find { it.row == pos.first && it.col == pos.second }
                target != null && target.color != piece.color
            }
        )
    }

    fun deselectPiece() {
        selectedPiece.value = null
        possibleMoves.clear()
        attackablePositions.clear()
    }

    fun moveSelectedTo(row: Int, col: Int) {
        selectedPiece.value?.let { piece ->
            val startRow = piece.row
            val startCol = piece.col

            // 🔵 Рокіровка першою
            if (piece.type == PieceType.KING && kotlin.math.abs(col - startCol) == 2) {
                if (col > startCol) {
                    val rook = pieces.find { it.type == PieceType.ROOK && it.color == piece.color && it.row == startRow && it.col == 7 }
                    rook?.let {
                        it.col = 5
                        if (!it.hasMoved) it.hasMoved = true
                        pieces.remove(it)
                        pieces.add(it)
                    }
                } else {
                    val rook = pieces.find { it.type == PieceType.ROOK && it.color == piece.color && it.row == startRow && it.col == 0 }
                    rook?.let {
                        it.col = 3
                        if (!it.hasMoved) it.hasMoved = true
                        pieces.remove(it)
                        pieces.add(it)
                    }
                }
            }

            // 🔸 Бій на проході
            if (piece.type == PieceType.PAWN && enPassantTarget.value == row to col) {
                val capturedRow = if (piece.color == PieceColor.WHITE) row + 1 else row - 1
                pieces.removeAll { it.row == capturedRow && it.col == col && it.color != piece.color }
            } else {
                pieces.removeAll { it.row == row && it.col == col && it.color != piece.color }
            }

            piece.row = row
            piece.col = col

            // 🔸 Промоція пішака
            if (piece.type == PieceType.PAWN) {
                val promotionRow = if (piece.color == PieceColor.WHITE) 0 else 7
                if (piece.row == promotionRow) {
                    pendingPromotion.value = piece
                    return
                }
            }

            // 🔸 Оновлення можливості бою на проході
            if (piece.type == PieceType.PAWN && kotlin.math.abs(row - startRow) == 2) {
                val dir = if (piece.color == PieceColor.WHITE) -1 else 1
                enPassantTarget.value = Pair(startRow + dir, startCol)
            } else {
                enPassantTarget.value = null
            }

            if (!piece.hasMoved) {
                piece.hasMoved = true
            }

            deselectPiece()
            updateGameState()
            currentTurn.value = currentTurn.value.opposite()
        }
    }


    fun updateGameState() {
        checkPosition.value = getCheckPosition(currentTurn.value.opposite())
        isMate.value = checkPosition.value != null && isCheckmate(currentTurn.value.opposite())

        if (!isMate.value && isStalemate(currentTurn.value.opposite())) {
            isStalemate.value = true
        }
    }

    fun getCheckPosition(color: PieceColor): Pair<Int, Int>? {
        val king = pieces.find { it.type == PieceType.KING && it.color == color } ?: return null
        val enemies = pieces.filter { it.color != color }
        return if (enemies.any { isMoveValid(it, king.row, king.col, pieces, enPassantTarget.value) }) {
            king.row to king.col
        } else null
    }

    fun isCheckmate(color: PieceColor): Boolean {
        val allies = pieces.filter { it.color == color }
        for (piece in allies) {
            val moves = generateMoves(piece)
            for ((r, c) in moves) {
                val snapshot = pieces.map { it.copy() }.toMutableList()
                val testPiece = snapshot.find { it == piece }!!
                snapshot.removeAll { it.row == r && it.col == c && it.color != piece.color }
                testPiece.row = r
                testPiece.col = c

                val king = snapshot.find { it.type == PieceType.KING && it.color == color } ?: continue
                if (snapshot.none { it.color != color && isMoveValid(it, king.row, king.col, snapshot, enPassantTarget.value) }) {
                    return false
                }
            }
        }
        return true
    }

    fun getClickedPiece(row: Int, col: Int): ChessPiece? =
        pieces.find { it.row == row && it.col == col }

    fun resetGame() {
        pieces.clear()
        pieces.addAll(initializePieces())
        selectedPiece.value = null
        possibleMoves.clear()
        attackablePositions.clear()
        currentTurn.value = PieceColor.WHITE
        checkPosition.value = null
        isMate.value = false
        isStalemate.value = false
    }

    fun promotePawn(newType: PieceType) {
        pendingPromotion.value?.let { pawn ->
            pieces.remove(pawn)
            pieces.add(
                ChessPiece(
                    type = newType,
                    color = pawn.color,
                    row = pawn.row,
                    col = pawn.col,
                    hasMoved = true
                )
            )
            pendingPromotion.value = null

            deselectPiece()
            updateGameState()
            currentTurn.value = currentTurn.value.opposite()
        }
    }

    private fun isStalemate(color: PieceColor): Boolean {
        val playerPieces = pieces.filter { it.color == color }

        return playerPieces.all { piece ->
            generateMoves(piece).isEmpty()
        } && getCheckPosition(color) == null
    }

    private fun generateMoves(piece: ChessPiece): List<Pair<Int, Int>> {
        val allMoves = (0 until 8).flatMap { r ->
            (0 until 8).mapNotNull { c ->
                if (isMoveValid(piece, r, c, pieces, enPassantTarget.value)) Pair(r, c) else null
            }
        }.toMutableList()

        // 🔥 Додаткова обробка для короля (рокіровка)
        if (piece.type == PieceType.KING && !piece.hasMoved) {
            // Додатково перевіряємо рокіровку

            val row = piece.row

            // Коротка рокіровка (направо)
            val rookKingside = pieces.find { it.type == PieceType.ROOK && it.color == piece.color && it.row == row && it.col == 7 && !it.hasMoved }
            if (rookKingside != null) {
                val pathClear = (piece.col + 1 until 7).all { col ->
                    pieces.none { it.row == row && it.col == col }
                }
                val safePath = (piece.col..piece.col + 2).all { col ->
                    isKingSafeAfterMove(piece, row, col)
                }
                if (pathClear && safePath) {
                    allMoves.add(row to piece.col + 2)
                }
            }

            // Довга рокіровка (наліво)
            val rookQueenside = pieces.find { it.type == PieceType.ROOK && it.color == piece.color && it.row == row && it.col == 0 && !it.hasMoved }
            if (rookQueenside != null) {
                val pathClear = (1 until piece.col).all { col ->
                    pieces.none { it.row == row && it.col == col }
                }
                val safePath = (piece.col downTo piece.col - 2).all { col ->
                    isKingSafeAfterMove(piece, row, col)
                }
                if (pathClear && safePath) {
                    allMoves.add(row to piece.col - 2)
                }
            }
        }

        // 🔵 Залишаємо тільки безпечні ходи
        return allMoves.filter { (r, c) ->
            val snapshot = pieces.map { it.copy() }.toMutableList()

            val moving = snapshot.find { it.row == piece.row && it.col == piece.col && it.color == piece.color }!!
            snapshot.removeAll { it.row == r && it.col == c && it.color != piece.color }
            moving.row = r
            moving.col = c

            val king = snapshot.find { it.type == PieceType.KING && it.color == piece.color }
            king != null && snapshot.none { it.color != piece.color && isMoveValid(it, king.row, king.col, snapshot, enPassantTarget.value) }
        }
    }

    private fun isKingSafeAfterMove(king: ChessPiece, row: Int, col: Int): Boolean {
        val snapshot = pieces.map { it.copy() }.toMutableList()
        val kingCopy = snapshot.find { it.row == king.row && it.col == king.col && it.color == king.color }!!
        kingCopy.row = row
        kingCopy.col = col

        return snapshot.none { it.color != king.color && isMoveValid(it, kingCopy.row, kingCopy.col, snapshot, enPassantTarget.value) }
    }




}

fun PieceColor.opposite(): PieceColor {
    return when (this) {
        PieceColor.WHITE -> PieceColor.BLACK
        PieceColor.BLACK -> PieceColor.WHITE
    }
}