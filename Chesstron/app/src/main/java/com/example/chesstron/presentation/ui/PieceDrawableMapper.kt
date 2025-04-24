package com.example.chesstron.presentation.ui

import androidx.compose.runtime.Composable
import com.example.chesstron.R
import com.example.chesstron.data.model.ChessPiece
import com.example.chesstron.data.model.PieceColor
import com.example.chesstron.data.model.PieceType

@Composable
fun getDrawableForPiece(piece: ChessPiece): Int {
    return when (piece.color) {
        PieceColor.WHITE -> when (piece.type) {
            PieceType.PAWN -> R.drawable.white_pawn
            PieceType.ROOK -> R.drawable.white_rook
            PieceType.KNIGHT -> R.drawable.white_knight
            PieceType.BISHOP -> R.drawable.white_bishop
            PieceType.QUEEN -> R.drawable.white_queen
            PieceType.KING -> R.drawable.white_king
        }

        PieceColor.BLACK -> when (piece.type) {
            PieceType.PAWN -> R.drawable.black_pawn
            PieceType.ROOK -> R.drawable.black_rook
            PieceType.KNIGHT -> R.drawable.black_knight
            PieceType.BISHOP -> R.drawable.black_bishop
            PieceType.QUEEN -> R.drawable.black_queen
            PieceType.KING -> R.drawable.black_king
        }
    }
}