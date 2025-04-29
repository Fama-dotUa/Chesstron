package com.example.chesstron.bots
import com.example.chesstron.data.model.ChessPiece
import com.example.chesstron.data.model.ChessRules
import com.example.chesstron.data.model.PieceColor

object BotEngine {

    fun chooseMove(
        pieces: List<ChessPiece>,
        botColor: PieceColor,
        enPassantTarget: Pair<Int, Int>?,
        playerColor: PieceColor
    ): Pair<Pair<Int, Int>, Pair<Int, Int>>? {
        val possibleMoves = mutableListOf<Pair<Pair<Int, Int>, Pair<Int, Int>>>()

        pieces.filter { it.color == botColor && it.row in 0..7 && it.col in 0..7 }.forEach { piece ->
        val moves = ChessRules.generateMoves(piece, pieces, enPassantTarget, playerColor)
            moves.forEach { move ->
                possibleMoves.add((piece.row to piece.col) to move)
            }
        }

        return possibleMoves.randomOrNull()
    }
}