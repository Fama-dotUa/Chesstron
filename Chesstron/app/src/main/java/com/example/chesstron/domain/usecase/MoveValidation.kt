package com.example.chesstron.domain.usecase

import com.example.chesstron.data.model.*

fun isMoveValid(
    piece: ChessPiece,
    toRow: Int,
    toCol: Int,
    pieces: List<ChessPiece>,
    enPassantTarget: Pair<Int, Int>?
): Boolean {
    val fromRow = piece.row
    val fromCol = piece.col

    if (fromRow == toRow && fromCol == toCol) return false

    val target = pieces.find { it.row == toRow && it.col == toCol }
    if (target?.color == piece.color) return false

    return when (piece.type) {
        PieceType.PAWN -> {
            val dir = if (piece.color == PieceColor.WHITE) -1 else 1
            val startRow = if (piece.color == PieceColor.WHITE) 6 else 1
            val oneStepForward = toRow == fromRow + dir && toCol == fromCol && target == null
            val twoStepForward = fromRow == startRow &&
                    toRow == fromRow + 2 * dir &&
                    toCol == fromCol &&
                    target == null &&
                    pieces.none { it.row == fromRow + dir && it.col == fromCol }
            val captureMove = toRow == fromRow + dir &&
                    kotlin.math.abs(toCol - fromCol) == 1 &&
                    target != null &&
                    target.color != piece.color

            // 🟡 НОВЕ: Бій на проході
            val enPassantCapture = enPassantTarget != null &&
                    toRow == enPassantTarget.first &&
                    toCol == enPassantTarget.second &&
                    kotlin.math.abs(fromCol - toCol) == 1 &&
                    fromRow + dir == toRow

            oneStepForward || twoStepForward || captureMove || enPassantCapture
        }


        PieceType.ROOK -> {
            (fromRow == toRow || fromCol == toCol) && isPathClear(fromRow, fromCol, toRow, toCol, pieces)
        }

        PieceType.BISHOP -> {
            kotlin.math.abs(fromRow - toRow) == kotlin.math.abs(fromCol - toCol) &&
                    isPathClear(fromRow, fromCol, toRow, toCol, pieces)
        }

        PieceType.QUEEN -> {
            (fromRow == toRow || fromCol == toCol ||
                    kotlin.math.abs(fromRow - toRow) == kotlin.math.abs(fromCol - toCol)) &&
                    isPathClear(fromRow, fromCol, toRow, toCol, pieces)
        }

        PieceType.KNIGHT -> {
            val dr = kotlin.math.abs(fromRow - toRow)
            val dc = kotlin.math.abs(fromCol - toCol)
            dr * dc == 2
        }

        PieceType.KING -> {
            kotlin.math.abs(fromRow - toRow) <= 1 && kotlin.math.abs(fromCol - toCol) <= 1
        }
    }
}



fun isPathClear(fromRow: Int, fromCol: Int, toRow: Int, toCol: Int, pieces: List<ChessPiece>): Boolean {
    val rowStep = Integer.signum(toRow - fromRow)
    val colStep = Integer.signum(toCol - fromCol)

    var currentRow = fromRow + rowStep
    var currentCol = fromCol + colStep

    while (currentRow != toRow || currentCol != toCol) {
        if (pieces.any { it.row == currentRow && it.col == currentCol }) return false
        currentRow += rowStep
        currentCol += colStep
    }

    return true
}
