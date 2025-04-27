package com.example.chesstron.data.model

object ChessRules {

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
                val enPassantCapture = enPassantTarget != null &&
                        toRow == enPassantTarget.first &&
                        toCol == enPassantTarget.second &&
                        kotlin.math.abs(fromCol - toCol) == 1 &&
                        fromRow + dir == toRow
                oneStepForward || twoStepForward || captureMove || enPassantCapture
            }

            PieceType.ROOK -> (fromRow == toRow || fromCol == toCol) && isPathClear(fromRow, fromCol, toRow, toCol, pieces)

            PieceType.BISHOP -> kotlin.math.abs(fromRow - toRow) == kotlin.math.abs(fromCol - toCol) &&
                    isPathClear(fromRow, fromCol, toRow, toCol, pieces)

            PieceType.QUEEN -> (fromRow == toRow || fromCol == toCol ||
                    kotlin.math.abs(fromRow - toRow) == kotlin.math.abs(fromCol - toCol)) &&
                    isPathClear(fromRow, fromCol, toRow, toCol, pieces)

            PieceType.KNIGHT -> {
                val dr = kotlin.math.abs(fromRow - toRow)
                val dc = kotlin.math.abs(fromCol - toCol)
                dr * dc == 2
            }

            PieceType.KING -> kotlin.math.abs(fromRow - toRow) <= 1 && kotlin.math.abs(fromCol - toCol) <= 1
        }
    }

    fun isPathClear(
        fromRow: Int,
        fromCol: Int,
        toRow: Int, toCol:
        Int, pieces:
        List<ChessPiece>
    ): Boolean {
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

    fun generateMoves(
        piece: ChessPiece,
        pieces: List<ChessPiece>,
        enPassantTarget: Pair<Int, Int>?
    ): List<Pair<Int, Int>> {
        val allMoves = (0 until 8).flatMap { r ->
            (0 until 8).mapNotNull { c ->
                if (isMoveValid(piece, r, c, pieces, enPassantTarget)) Pair(r, c) else null
            }
        }.toMutableList()

        // Рокіровка для короля
        if (piece.type == PieceType.KING && !piece.hasMoved) {
            val row = piece.row

            val rookKingside = pieces.find { it.type == PieceType.ROOK && it.color == piece.color && it.row == row && it.col == 7 && !it.hasMoved }
            if (rookKingside != null) {
                val pathClear = (piece.col + 1 until 7).all { col ->
                    pieces.none { it.row == row && it.col == col }
                }
                if (pathClear && isKingSafeDuringCastling(piece, pieces, true)) {
                    allMoves.add(row to piece.col + 2)
                }
            }

            val rookQueenside = pieces.find { it.type == PieceType.ROOK && it.color == piece.color && it.row == row && it.col == 0 && !it.hasMoved }
            if (rookQueenside != null) {
                val pathClear = (1 until piece.col).all { col ->
                    pieces.none { it.row == row && it.col == col }
                }
                if (pathClear && isKingSafeDuringCastling(piece, pieces, false)) {
                    allMoves.add(row to piece.col - 2)
                }
            }
        }

        return allMoves.filter { (r, c) ->
            val snapshot = pieces.map { it.copy() }.toMutableList()
            val moving = snapshot.find { it.row == piece.row && it.col == piece.col && it.color == piece.color } ?: return@filter false

            snapshot.removeAll { it.row == r && it.col == c && it.color != piece.color }
            moving.row = r
            moving.col = c

            val king = snapshot.find { it.type == PieceType.KING && it.color == piece.color }
            king != null && snapshot.none { it.color != piece.color && isMoveValid(it, king.row, king.col, snapshot, enPassantTarget) }
        }
    }

    private fun isKingSafeDuringCastling(
        king: ChessPiece,
        pieces: List<ChessPiece>,
        kingside: Boolean
    ): Boolean {
        val snapshot = pieces.map { it.copy() }.toMutableList()
        val kingCopy = snapshot.find { it.type == PieceType.KING && it.color == king.color } ?: return false

        val colsToCheck = if (kingside) listOf(king.col, king.col + 1, king.col + 2)
        else listOf(king.col, king.col - 1, king.col - 2)

        return colsToCheck.all { col ->
            kingCopy.col = col
            snapshot.none { it.color != king.color && isMoveValid(it, kingCopy.row, kingCopy.col, snapshot, null) }
        }
    }

    fun getCheckPosition(
        color: PieceColor,
        pieces: List<ChessPiece>,
        enPassantTarget: Pair<Int, Int>?
    ): Pair<Int, Int>? {
        val king = pieces.find { it.type == PieceType.KING && it.color == color } ?: return null
        val enemies = pieces.filter { it.color != color }
        return if (enemies.any { isMoveValid(it, king.row, king.col, pieces, enPassantTarget) }) {
            king.row to king.col
        } else null
    }

    fun isCheckmate(
        color: PieceColor,
        pieces: List<ChessPiece>,
        enPassantTarget: Pair<Int, Int>?
    ): Boolean {
        val allies = pieces.filter { it.color == color }
        for (piece in allies) {
            val moves = generateMoves(piece, pieces, enPassantTarget)
            for ((r, c) in moves) {
                val snapshot = pieces.map { it.copy() }.toMutableList()
                val testPiece = snapshot.find { it == piece } ?: continue
                snapshot.removeAll { it.row == r && it.col == c && it.color != piece.color }
                testPiece.row = r
                testPiece.col = c

                val king = snapshot.find { it.type == PieceType.KING && it.color == color } ?: continue
                if (snapshot.none { it.color != color && isMoveValid(it, king.row, king.col, snapshot, enPassantTarget) }) {
                    return false
                }
            }
        }
        return true
    }

    fun isStalemate(
        color: PieceColor,
        pieces: List<ChessPiece>,
        enPassantTarget: Pair<Int, Int>?
    ): Boolean {
        val allies = pieces.filter { it.color == color }
        return allies.all { generateMoves(it, pieces, enPassantTarget).isEmpty() } &&
                getCheckPosition(color, pieces, enPassantTarget) == null
    }
}

