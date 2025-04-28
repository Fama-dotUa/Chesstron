package com.example.chesstron.data.model

data class MoveRecord(
    val pieceType: PieceType,
    val pieceColor: PieceColor,
    val fromRow: Int,
    val fromCol: Int,
    val toRow: Int,
    val toCol: Int,
    val capturedPieceType: PieceType? = null,
    val capturedPieceColor: PieceColor? = null,
    val isPromotion: Boolean = false,
    val promotionType: PieceType? = null,
    val isCastle: Boolean = false,
    val isCheck: Boolean = false,
    val isCheckmate: Boolean = false,
    val isStalemate: Boolean = false
)
