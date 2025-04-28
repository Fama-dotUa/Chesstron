package com.example.chesstron.data

sealed class GameEvent {
    object MoveMade : GameEvent()
    object CaptureMade : GameEvent()
    object Check : GameEvent()
    object Checkmate : GameEvent()
    object Stalemate : GameEvent()
}