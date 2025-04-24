package com.example.chesstron.domain.usecase

import com.example.chesstron.data.model.ChessPiece
import com.example.chesstron.data.model.PieceColor
import com.example.chesstron.data.model.PieceType

fun initializePieces(): List<ChessPiece> {
    val result = mutableListOf<ChessPiece>()

    // Білі фігури
    result.addAll(List(8) { col -> ChessPiece(PieceType.PAWN, PieceColor.WHITE, 6, col) })
    result.addAll(
        listOf(
            ChessPiece(PieceType.ROOK, PieceColor.WHITE, 7, 0),
            ChessPiece(PieceType.KNIGHT, PieceColor.WHITE, 7, 1),
            ChessPiece(PieceType.BISHOP, PieceColor.WHITE, 7, 2),
            ChessPiece(PieceType.QUEEN, PieceColor.WHITE, 7, 3),
            ChessPiece(PieceType.KING, PieceColor.WHITE, 7, 4),
            ChessPiece(PieceType.BISHOP, PieceColor.WHITE, 7, 5),
            ChessPiece(PieceType.KNIGHT, PieceColor.WHITE, 7, 6),
            ChessPiece(PieceType.ROOK, PieceColor.WHITE, 7, 7),
        )
    )

    // Чорні фігури
    result.addAll(List(8) { col -> ChessPiece(PieceType.PAWN, PieceColor.BLACK, 1, col) })
    result.addAll(
        listOf(
            ChessPiece(PieceType.ROOK, PieceColor.BLACK, 0, 0),
            ChessPiece(PieceType.KNIGHT, PieceColor.BLACK, 0, 1),
            ChessPiece(PieceType.BISHOP, PieceColor.BLACK, 0, 2),
            ChessPiece(PieceType.QUEEN, PieceColor.BLACK, 0, 3),
            ChessPiece(PieceType.KING, PieceColor.BLACK, 0, 4),
            ChessPiece(PieceType.BISHOP, PieceColor.BLACK, 0, 5),
            ChessPiece(PieceType.KNIGHT, PieceColor.BLACK, 0, 6),
            ChessPiece(PieceType.ROOK, PieceColor.BLACK, 0, 7),
        )
    )

    return result
}