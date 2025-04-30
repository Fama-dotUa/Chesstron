package com.example.chesstron.presentation.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chesstron.bots.BotEngine
import com.example.chesstron.data.GameEvent
import com.example.chesstron.data.GameMode
import com.example.chesstron.data.model.ChessPiece
import com.example.chesstron.data.model.ChessRules
import com.example.chesstron.data.model.GameSession
import com.example.chesstron.data.model.GameState
import com.example.chesstron.data.model.MoveRecord
import com.example.chesstron.data.model.PieceColor
import com.example.chesstron.data.model.PieceType
import com.example.chesstron.domain.usecase.initializePieces
import com.example.chesstron.domain.usecase.initializePiecesForPlayer
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class ChessBoardViewModel : ViewModel() {
    var lastEvent = mutableStateOf<GameEvent?>(null)
        private set

    var gameState = mutableStateOf(GameState())
        private set
    var gameMode: GameMode = GameMode.SINGLE_DEVICE
    var playerColor: PieceColor = PieceColor.WHITE
    val moveHistory = mutableListOf<MoveRecord>()
    private val firestore = FirebaseFirestore.getInstance()
    var currentGameId: String? = null
    private var skipNextSnapshot = false
    private var suppressOnlineSync = false

    init {
        if (gameState.value.pieces.isEmpty()) {
            resetGame()
        }
    }


    fun selectPiece(piece: ChessPiece, bypassCheck: Boolean = false) {
        if (!bypassCheck) {
            if (gameMode == GameMode.VS_COMPUTER && piece.color != playerColor) return
            if (gameMode == GameMode.ONLINE && piece.color != playerColor) return
            if (gameMode == GameMode.ONLINE && gameState.value.currentTurn != playerColor) return
            if (piece.color != gameState.value.currentTurn || gameState.value.gameOver) return
        }

        val moves = ChessRules.generateMoves(piece, gameState.value.pieces, gameState.value.enPassantTarget, playerColor)

        val filteredMoves = if (gameState.value.checkPosition != null) {
            moves.filter { move -> isMoveSafe(piece, move.first, move.second) }
        } else moves

        gameState.value = gameState.value.copy(
            selectedPiece = piece,
            possibleMoves = filteredMoves,
            attackablePositions = filteredMoves.filter { pos ->
                gameState.value.pieces.any { it.row == pos.first && it.col == pos.second && it.color != piece.color }
            }
        )
    }

    fun deselectPiece() {
        gameState.value = gameState.value.copy(
            selectedPiece = null,
            possibleMoves = emptyList(),
            attackablePositions = emptyList()
        )
    }

    fun moveSelectedTo(row: Int, col: Int, promotion: PieceType? = null) {
        val piece = gameState.value.selectedPiece ?: return
        val wasCapture = gameState.value.pieces.any { it.row == row && it.col == col && it.color != piece.color }
        if (!gameState.value.possibleMoves.contains(row to col)) return

        val startRow = piece.row
        val startCol = piece.col

        if (piece.type == PieceType.KING && kotlin.math.abs(col - startCol) == 2) {
            val rookCol = if (col > startCol) 7 else 0
            val newRookCol = if (col > startCol) 5 else 3
            val rook = gameState.value.pieces.find { it.row == startRow && it.col == rookCol && it.color == piece.color }
            rook?.let {
                it.col = newRookCol
                it.hasMoved = true
            }
        }

        val enPassantTarget = gameState.value.enPassantTarget

        if (piece.type == PieceType.PAWN && enPassantTarget == row to col) {
            val capturedRow = if (piece.color == PieceColor.WHITE) row + 1 else row - 1
            gameState.value.pieces.find { it.row == capturedRow && it.col == col && it.color != piece.color }?.apply {
                this.row = -1
                this.col = -1
            }
        } else {
            gameState.value.pieces.find { it.row == row && it.col == col && it.color != piece.color }?.apply {
                this.row = -1
                this.col = -1
            }
        }

        val capturedPiece = gameState.value.pieces.find { it.row == row && it.col == col && it.color != piece.color }
        val isCastleMove = piece.type == PieceType.KING && kotlin.math.abs(col - piece.col) == 2

        moveHistory.add(
            MoveRecord(
                pieceType = piece.type,
                pieceColor = piece.color,
                fromRow = piece.row,
                fromCol = piece.col,
                toRow = row,
                toCol = col,
                capturedPieceType = capturedPiece?.type,
                capturedPieceColor = capturedPiece?.color,
                isPromotion = false,
                isCastle = isCastleMove
            )
        )

        piece.row = row
        piece.col = col
        piece.hasMoved = true

        val promotionRow = if (piece.color == PieceColor.WHITE) 0 else 7
        if (piece.type == PieceType.PAWN && piece.row == promotionRow) {
            if (promotion != null) {
                piece.type = promotion
                piece.hasMoved = true

                moveHistory[moveHistory.lastIndex] = moveHistory.last().copy(
                    isPromotion = true,
                    promotionType = promotion
                )

                if (gameMode == GameMode.ONLINE && !suppressOnlineSync) {
                    sendMoveOnline(startRow to startCol, row to col, promotion)
                }
            } else {
                gameState.value = gameState.value.copy(
                    pendingPromotion = piece,
                    selectedPiece = null,
                    possibleMoves = emptyList(),
                    attackablePositions = emptyList()
                )
                return
            }
        } else if (gameMode == GameMode.ONLINE && !suppressOnlineSync) {
            sendMoveOnline(startRow to startCol, row to col)
        }

        val newEnPassantTarget = if (piece.type == PieceType.PAWN && kotlin.math.abs(row - startRow) == 2) {
            val dir = if (piece.color == PieceColor.WHITE) -1 else 1
            Pair(startRow + dir, startCol)
        } else null

        gameState.value = gameState.value.copy(
            selectedPiece = null,
            possibleMoves = emptyList(),
            attackablePositions = emptyList(),
            enPassantTarget = newEnPassantTarget,
            lastMove = (startRow to startCol) to (row to col)
        )

        lastEvent.value = if (wasCapture) GameEvent.CaptureMade else GameEvent.MoveMade

        updateGameState()

        gameState.value = gameState.value.copy(
            currentTurn = gameState.value.currentTurn.opposite()
        )

        val isPlayerMove = gameState.value.currentTurn == playerColor
        if (!isPlayerMove && gameMode == GameMode.VS_COMPUTER) {
            makeBotMove()
        }
    }
    fun promotePawn(newType: PieceType) {
        val pawn = gameState.value.pendingPromotion ?: return

        // Створюємо нову фігуру замість пішака
        val promotedPiece = ChessPiece(
            type = newType,
            color = pawn.color,
            initialRow = pawn.row,
            initialCol = pawn.col,
            hasMoved = true
        ).apply {
            row = pawn.row
            col = pawn.col
        }
        // Оновлюємо запис в історії, якщо треба
        val lastMove = moveHistory.lastOrNull()
        if (lastMove != null) {
            moveHistory[moveHistory.lastIndex] = lastMove.copy(
                isPromotion = true,
                promotionType = newType
            )
        }

        val from = if (lastMove != null) lastMove.fromRow to lastMove.fromCol else pawn.row to pawn.col
        val to = pawn.row to pawn.col

        if (gameMode == GameMode.ONLINE) {
            sendMoveOnline(
                from = from,
                to = to,
                promotion = newType
            )
        }
        // Створюємо новий список фігур, замінюючи пішака на нову фігуру
        val updatedPieces = gameState.value.pieces.map {
            if (it === pawn) promotedPiece else it
        }

        // Оновлюємо GameState
        gameState.value = gameState.value.copy(
            pieces = updatedPieces,
            pendingPromotion = null
        )

        updateGameState()

        gameState.value = gameState.value.copy(
            currentTurn = gameState.value.currentTurn.opposite()
        )

        val isPlayerMove = gameState.value.currentTurn == playerColor
        if (!isPlayerMove && gameMode == GameMode.VS_COMPUTER) {
            makeBotMove()
        }
    }

    private fun updateGameState() {
        val opponentColor = gameState.value.currentTurn.opposite()
        val checkPos = ChessRules.getCheckPosition(opponentColor, gameState.value.pieces, gameState.value.enPassantTarget, playerColor)
        val mate = checkPos != null && ChessRules.isCheckmate(opponentColor, gameState.value.pieces, gameState.value.enPassantTarget, playerColor)
        val stalemate = !mate && ChessRules.isStalemate(opponentColor, gameState.value.pieces, gameState.value.enPassantTarget, playerColor)
        if (checkPos != null && !mate) {
            lastEvent.value = GameEvent.Check
        }
        if (mate) {
            lastEvent.value = GameEvent.Checkmate
        }
        if (stalemate) {
            lastEvent.value = GameEvent.Stalemate
        }
        val last = moveHistory.lastOrNull()
        if (last != null) {
            moveHistory[moveHistory.lastIndex] = last.copy(
                isCheck = gameState.value.checkPosition != null,
                isCheckmate = gameState.value.isMate,
                isStalemate = gameState.value.isStalemate
            )
        }

        gameState.value = gameState.value.copy(
            checkPosition = checkPos,
            isMate = mate,
            isStalemate = stalemate,
            gameOver = mate || stalemate
        )
    }

    fun resetGame(gameMode: GameMode = GameMode.SINGLE_DEVICE, playerColor: PieceColor = PieceColor.WHITE) {
        moveHistory.clear()

        val pieces = if (gameMode == GameMode.VS_COMPUTER) {
            initializePiecesForPlayer(playerColor)
        } else {
            initializePieces()
        }
        this.gameMode = gameMode
        this.playerColor = playerColor
        gameState.value = GameState(
            pieces = pieces,
            currentTurn = PieceColor.WHITE
        )
        // Якщо гравець обрав чорних, то бот починає перший
        if (gameMode == GameMode.VS_COMPUTER && playerColor == PieceColor.BLACK) {
            makeBotMove()
        }

    }

    fun getClickedPiece(row: Int, col: Int): ChessPiece? =
        gameState.value.pieces.find { it.row == row && it.col == col }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun undoMove() {
        if (moveHistory.isEmpty()) return
        val lastMove = moveHistory.removeLast()

        val movingPiece = gameState.value.pieces.find {
            it.row == lastMove.toRow && it.col == lastMove.toCol && it.color == lastMove.pieceColor
        } ?: return

        // Відміняємо промоцію
        if (lastMove.isPromotion && lastMove.promotionType != null) {
            movingPiece.type = PieceType.PAWN
        }

        // Повертаємо фігуру назад
        movingPiece.row = lastMove.fromRow
        movingPiece.col = lastMove.fromCol

        // Якщо була зʼїдена фігура — відновлюємо її
        if (lastMove.capturedPieceType != null && lastMove.capturedPieceColor != null) {
            val newPieces = gameState.value.pieces.toMutableList()

            newPieces.add(
                ChessPiece(
                    type = lastMove.capturedPieceType,
                    color = lastMove.capturedPieceColor,
                    initialRow = lastMove.toRow,
                    initialCol = lastMove.toCol,
                    hasMoved = false
                )
            )

            gameState.value = gameState.value.copy(
                pieces = newPieces,
                currentTurn = gameState.value.currentTurn.opposite(),
                selectedPiece = null,
                possibleMoves = emptyList(),
                attackablePositions = emptyList(),
                pendingPromotion = null,
                enPassantTarget = null,
                lastMove = null
            )

        }

        // Якщо була рокіровка — повертаємо туру
        if (lastMove.isCastle) {
            val rookStartCol = if (lastMove.toCol > lastMove.fromCol) 7 else 0
            val rookEndCol = if (lastMove.toCol > lastMove.fromCol) 5 else 3
            val rook = gameState.value.pieces.find { it.row == lastMove.toRow && it.col == rookEndCol && it.color == lastMove.pieceColor }
            rook?.col = rookStartCol
        }

        gameState.value = gameState.value.copy(
            currentTurn = gameState.value.currentTurn.opposite(),
            selectedPiece = null,
            possibleMoves = emptyList(),
            attackablePositions = emptyList(),
            pendingPromotion = null,
            enPassantTarget = null,
            lastMove = null
        )
    }

    fun makeBotMove() {
        viewModelScope.launch {
            delay(500L)

            if (gameState.value.gameOver) return@launch
            if (gameMode != GameMode.VS_COMPUTER) return@launch
            if (gameState.value.currentTurn != playerColor.opposite()) return@launch

            val move = BotEngine.chooseMove(
                pieces = gameState.value.pieces,
                botColor = playerColor.opposite(),
                enPassantTarget = gameState.value.enPassantTarget,
                playerColor = playerColor
            ) ?: return@launch

            val (from, to) = move
            val piece = getClickedPiece(from.first, from.second) ?: return@launch
            selectPiece(piece, bypassCheck = true)

            val promotionRow = if (piece.color == PieceColor.WHITE) 0 else 7
            val isPromotion = piece.type == PieceType.PAWN && to.first == promotionRow
            val promotionType = if (isPromotion) PieceType.QUEEN else null

            moveSelectedTo(to.first, to.second, promotion = promotionType)
        }
    }

    private fun isMoveSafe(piece: ChessPiece, toRow: Int, toCol: Int): Boolean {
        val snapshot = gameState.value.pieces.map { it.copy() }.toMutableList()
        val moving = snapshot.find { it.row == piece.row && it.col == piece.col && it.color == piece.color } ?: return false

        snapshot.removeAll { it.row == toRow && it.col == toCol && it.color != piece.color }
        moving.row = toRow
        moving.col = toCol

        val king = snapshot.find { it.type == PieceType.KING && it.color == piece.color } ?: return false

        return snapshot.none { opponent ->
            opponent.color != piece.color && ChessRules.isMoveValid(opponent, king.row, king.col, snapshot, gameState.value.enPassantTarget, playerColor)
        }
    }

    fun joinOnlineGame(gameId: String, playerColor: PieceColor) {
        viewModelScope.launch {
            resetGame(GameMode.ONLINE, playerColor)

            firestore.collection("games").document(gameId)
                .update(
                    if (playerColor == PieceColor.WHITE) "playerWhiteId" else "playerBlackId",
                    "guest",
                    "status",
                    "playing"
                ).await()
            currentGameId = gameId

            listenToOnlineGame(gameId)
            Log.d("FIREBASE", "Joined game with ID: $gameId")
        }
    }

    private fun listenToOnlineGame(gameId: String) {
        firestore.collection("games").document(gameId)
            .addSnapshotListener { snapshot, error ->
                if (skipNextSnapshot) {
                    skipNextSnapshot = false
                    return@addSnapshotListener
                }

                val session = snapshot?.toObject(GameSession::class.java) ?: return@addSnapshotListener
                val movesFromFirestore = session.moves

                if (movesFromFirestore.size > moveHistory.size) {
                    val moveEntry = movesFromFirestore.last() as? Map<*, *> ?: return@addSnapshotListener
                    val moveString = moveEntry["move"] as? String ?: return@addSnapshotListener
                    val byPlayer = moveEntry["by"] as? String ?: return@addSnapshotListener
                    if (byPlayer == playerColor.name.lowercase()) return@addSnapshotListener

                    if (moveString.length == 4) {
                        val fromRow = moveString[0].digitToInt()
                        val fromCol = moveString[1].digitToInt()
                        val toRow = moveString[2].digitToInt()
                        val toCol = moveString[3].digitToInt()

                        val expectedColor = if (byPlayer == "white") PieceColor.WHITE else PieceColor.BLACK
                        val piece = gameState.value.pieces.find {
                            it.row == fromRow && it.col == fromCol && it.color == expectedColor
                        } ?: return@addSnapshotListener

                        val promotionType = (moveEntry["promotion"] as? String)?.let {
                            PieceType.valueOf(it.uppercase())
                        }

                        suppressOnlineSync = true
                        selectPiece(piece, bypassCheck = true)
                        moveSelectedTo(toRow, toCol, promotion = promotionType)
                        suppressOnlineSync = false
                    }
                }
            }
    }


    fun sendMoveOnline(from: Pair<Int, Int>, to: Pair<Int, Int>, promotion: PieceType? = null) {
        val moveString = "${from.first}${from.second}${to.first}${to.second}"

        val moveData = mutableMapOf(
            "move" to moveString,
            "by" to playerColor.name.lowercase()
        )

        promotion?.let {
            moveData["promotion"] = it.name.lowercase()
        }

        currentGameId?.let { id ->
            skipNextSnapshot = true
            firestore.collection("games").document(id)
                .update(
                    "moves", FieldValue.arrayUnion(moveData),
                    "turn", if (gameState.value.currentTurn == PieceColor.WHITE) "black" else "white"
                )
        }
    }

    fun createLobby(name: String, password: String?, playerColor: PieceColor) {
        viewModelScope.launch {
            resetGame(GameMode.ONLINE, playerColor)
            val gameId = firestore.collection("games").document().id

            val session = GameSession(
                gameId = gameId,
                name = name,
                password = password,
                playerWhiteId = if (playerColor == PieceColor.WHITE) "host" else "",
                playerBlackId = if (playerColor == PieceColor.BLACK) "host" else "",
                turn = "white",
                status = "waiting",
                createdAt = System.currentTimeMillis()
            )

            firestore.collection("games").document(gameId).set(session).await()
            currentGameId = gameId
            listenToOnlineGame(gameId)
            Log.d("FIREBASE", "Лобі створено: $name ($gameId)")
        }
    }

}

fun PieceColor.opposite(): PieceColor {
    return when (this) {
        PieceColor.WHITE -> PieceColor.BLACK
        PieceColor.BLACK -> PieceColor.WHITE
    }
}