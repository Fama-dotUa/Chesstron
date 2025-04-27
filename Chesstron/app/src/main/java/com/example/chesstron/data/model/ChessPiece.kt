package com.example.chesstron.data.model

enum class PieceType { PAWN, ROOK, KNIGHT, BISHOP, QUEEN, KING }
enum class PieceColor { WHITE, BLACK;

}

data class ChessPiece(
    var type: PieceType,
    val color: PieceColor,
    var row: Int,
    var col: Int,
    var hasMoved: Boolean = false
)