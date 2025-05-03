package com.example.chesstron.domain.usecase

import com.example.chesstron.data.model.ChessPiece
import com.example.chesstron.data.model.PieceColor
import com.example.chesstron.presentation.viewmodel.opposite

fun initializePiecesForPlayer(playerColor: PieceColor): List<ChessPiece> {

    return initializePieces()
    /*val pieces = initializePieces()
    return if (playerColor == PieceColor.WHITE) {
        pieces
    } else {
        pieces.map { piece ->
            piece.copy(
                row = 7 - piece.row,
                col = 7 - piece.col
            )
        }
    }*/
}
