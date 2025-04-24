package com.example.chesstron.presentation.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.example.chesstron.data.model.ChessPiece

@Composable
fun ChessPieceView(
    piece: ChessPiece,
    cellSize: Dp,
    isSelected: Boolean
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.15f else 1f,
        animationSpec = tween(durationMillis = 200),
        label = "PieceScale"
    )

    Box(
        modifier = Modifier
            .absoluteOffset(
                x = piece.col * cellSize,
                y = piece.row * cellSize
            )
            .size(cellSize)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                translationY = if (isSelected) -8f else 0f
            }
    ) {
        Image(
            painter = painterResource(id = getDrawableForPiece(piece)),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
    }
}

