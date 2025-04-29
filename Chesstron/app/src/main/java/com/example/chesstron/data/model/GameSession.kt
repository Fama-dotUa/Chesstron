package com.example.chesstron.data.model

data class GameSession(
    val gameId: String = "",
    val name: String = "",
    val password: String? = null,
    val playerWhiteId: String = "",
    val playerBlackId: String = "",
    val moves: List<Map<String, String>> = emptyList(),
    val turn: String = "white",
    val status: String = "waiting",
    val createdAt: Long = System.currentTimeMillis()
)
