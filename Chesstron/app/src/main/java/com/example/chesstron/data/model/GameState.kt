package com.example.chesstron.data.model

data class GameState(
    val pieces: List<ChessPiece> = emptyList(),
    val selectedPiece: ChessPiece? = null,
    val possibleMoves: List<Pair<Int, Int>> = emptyList(),
    val attackablePositions: List<Pair<Int, Int>> = emptyList(),
    val currentTurn: PieceColor = PieceColor.WHITE,
    val enPassantTarget: Pair<Int, Int>? = null,
    val pendingPromotion: ChessPiece? = null,
    val checkPosition: Pair<Int, Int>? = null,
    val isMate: Boolean = false,
    val isStalemate: Boolean = false,
    val gameOver: Boolean = false,
    var lastMove: Pair<Pair<Int, Int>, Pair<Int, Int>>? = null
)