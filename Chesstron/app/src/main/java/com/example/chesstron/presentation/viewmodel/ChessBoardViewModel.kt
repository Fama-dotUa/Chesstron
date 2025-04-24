package com.example.chesstron.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.chesstron.data.model.ChessPiece
import com.example.chesstron.domain.usecase.initializePieces
import com.example.chesstron.domain.usecase.isMoveValid

class ChessBoardViewModel : ViewModel() {

    var pieces = mutableStateListOf<ChessPiece>()
        private set

    var selectedPiece = mutableStateOf<ChessPiece?>(null)
        private set

    var possibleMoves = mutableStateListOf<Pair<Int, Int>>()
        private set

    var attackablePositions = mutableStateListOf<Pair<Int, Int>>()
        private set

    init {
        if (pieces.isEmpty()) {
            pieces.addAll(initializePieces())
        }
    }

    fun selectPiece(piece: ChessPiece) {
        selectedPiece.value = piece
        possibleMoves.clear()
        attackablePositions.clear()

        val moves = generateMoves(piece)
        possibleMoves.addAll(moves)

        attackablePositions.addAll(
            moves.filter { pos ->
                val target = pieces.find { it.row == pos.first && it.col == pos.second }
                target != null && target.color != piece.color
            }
        )
    }

    fun deselectPiece() {
        selectedPiece.value = null
        possibleMoves.clear()
        attackablePositions.clear()
    }

    fun moveSelectedTo(row: Int, col: Int) {
        selectedPiece.value?.let { piece ->
            pieces.removeAll { it.row == row && it.col == col && it.color != piece.color }
            piece.row = row
            piece.col = col
            deselectPiece()
        }
    }

    fun getClickedPiece(row: Int, col: Int): ChessPiece? =
        pieces.find { it.row == row && it.col == col }

    private fun generateMoves(piece: ChessPiece): List<Pair<Int, Int>> {
        return (0 until 8).flatMap { r ->
            (0 until 8).mapNotNull { c ->
                if (isMoveValid(piece, r, c, pieces))
                    Pair(r, c)
                else null
            }
        }
    }
}
