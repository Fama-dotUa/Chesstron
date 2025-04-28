package com.example.chesstron.data.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue

enum class PieceType { PAWN, ROOK, KNIGHT, BISHOP, QUEEN, KING }
enum class PieceColor { WHITE, BLACK;}

class ChessPiece(
    var type: PieceType,
    val color: PieceColor,
    initialRow: Int,
    initialCol: Int,
    var hasMoved: Boolean = false
) {
    var row by mutableIntStateOf(initialRow)
    var col by mutableIntStateOf(initialCol)

    fun copy(
        type: PieceType = this.type,
        color: PieceColor = this.color,
        row: Int = this.row,
        col: Int = this.col,
        hasMoved: Boolean = this.hasMoved
    ): ChessPiece {
        return ChessPiece(type, color, row, col, hasMoved)
    }
}