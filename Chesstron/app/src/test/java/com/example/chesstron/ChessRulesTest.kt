package com.example.chesstron

import com.example.chesstron.data.model.ChessPiece
import com.example.chesstron.data.model.ChessRules
import com.example.chesstron.data.model.PieceColor
import com.example.chesstron.data.model.PieceType
import org.junit.Assert.*
import org.junit.Test
class ChessRulesTest {

    @Test
    fun testPawnMoveForwardOneStep() {
        val pawn = ChessPiece(PieceType.PAWN, PieceColor.WHITE, 6, 4)
        val king = ChessPiece(PieceType.KING, PieceColor.WHITE, 7, 7)
        val pieces = listOf(pawn, king)

        val moves = ChessRules.generateMoves(pawn, pieces, null)

        assertTrue(moves.contains(5 to 4))
    }

    @Test
    fun testRookMoveHorizontally() {
        val rook = ChessPiece(PieceType.ROOK, PieceColor.WHITE, 4, 4)
        val king = ChessPiece(PieceType.KING, PieceColor.WHITE, 7, 7)
        val pieces = listOf(rook, king)

        val moves = ChessRules.generateMoves(rook, pieces, null)

        assertTrue(moves.contains(4 to 0))
        assertTrue(moves.contains(4 to 7))
    }

    @Test
    fun testKingCannotMoveIntoCheck() {
        val king = ChessPiece(PieceType.KING, PieceColor.WHITE, 7, 4)
        val enemyRook = ChessPiece(PieceType.ROOK, PieceColor.BLACK, 5, 4)
        val pieces = listOf(king, enemyRook)

        val moves = ChessRules.generateMoves(king, pieces, null)

        assertFalse(moves.contains(6 to 4))
    }

    @Test
    fun testCastlingAvailableWhenSafe() {
        val king = ChessPiece(PieceType.KING, PieceColor.WHITE, 7, 4)
        val rook = ChessPiece(PieceType.ROOK, PieceColor.WHITE, 7, 7)
        val pieces = listOf(king, rook)

        val moves = ChessRules.generateMoves(king, pieces, null)

        assertTrue(moves.contains(7 to 6))
    }

    @Test
    fun testPawnCapture() {
        val pawn = ChessPiece(PieceType.PAWN, PieceColor.WHITE, 6, 4)
        val king = ChessPiece(PieceType.KING, PieceColor.WHITE, 7, 7)
        val enemyPawn = ChessPiece(PieceType.PAWN, PieceColor.BLACK, 5, 5)
        val pieces = listOf(pawn, king, enemyPawn)

        val moves = ChessRules.generateMoves(pawn, pieces, null)

        assertTrue(moves.contains(5 to 5))
    }

    @Test
    fun testCheckDetection() {
        val king = ChessPiece(PieceType.KING, PieceColor.WHITE, 7, 4)
        val rook = ChessPiece(PieceType.ROOK, PieceColor.BLACK, 5, 4)
        val pieces = listOf(king, rook)

        val check = ChessRules.getCheckPosition(PieceColor.WHITE, pieces, null)

        assertNotNull(check)
    }

    @Test
    fun testNoCheckWhenSafe() {
        val king = ChessPiece(PieceType.KING, PieceColor.WHITE, 7, 4)
        val rook = ChessPiece(PieceType.ROOK, PieceColor.BLACK, 5, 2)
        val pieces = listOf(king, rook)

        val check = ChessRules.getCheckPosition(PieceColor.WHITE, pieces, null)

        assertNull(check)
    }

    @Test
    fun testPawnPromotionAvailable() {
        val pawn = ChessPiece(PieceType.PAWN, PieceColor.WHITE, 1, 4)
        val king = ChessPiece(PieceType.KING, PieceColor.WHITE, 7, 7)
        val pieces = listOf(pawn, king)

        val moves = ChessRules.generateMoves(pawn, pieces, null)

        assertTrue(moves.contains(0 to 4))
    }

    @Test
    fun testStalemateDetection() {
        val whiteKing = ChessPiece(PieceType.KING, PieceColor.WHITE, 0, 0)
        val blackQueen = ChessPiece(PieceType.QUEEN, PieceColor.BLACK, 1, 2)
        val blackKing = ChessPiece(PieceType.KING, PieceColor.BLACK, 2, 2)
        val pieces = listOf(whiteKing, blackQueen, blackKing)

        val isStalemate = ChessRules.isStalemate(PieceColor.WHITE, pieces, null)

        assertTrue(isStalemate)
    }

    @Test
    fun testCheckmateDetection() {
        val whiteKing = ChessPiece(PieceType.KING, PieceColor.WHITE, 0, 0)
        val blackQueen = ChessPiece(PieceType.QUEEN, PieceColor.BLACK, 1, 1)
        val blackKing = ChessPiece(PieceType.KING, PieceColor.BLACK, 2, 2)
        val pieces = listOf(whiteKing, blackQueen, blackKing)

        val isCheckmate = ChessRules.isCheckmate(PieceColor.WHITE, pieces, null)

        assertTrue(isCheckmate)
    }

    @Test
    fun testNoCastlingThroughCheck() {
        val king = ChessPiece(PieceType.KING, PieceColor.WHITE, 7, 4)
        val rook = ChessPiece(PieceType.ROOK, PieceColor.WHITE, 7, 7)
        val enemyRook = ChessPiece(PieceType.ROOK, PieceColor.BLACK, 5, 5)
        val pieces = listOf(king, rook, enemyRook)

        val moves = ChessRules.generateMoves(king, pieces, null)

        assertFalse(moves.contains(7 to 6))
    }
}

