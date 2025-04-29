package com.example.chesstron.presentation.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.example.chesstron.data.model.ChessPiece
import com.example.chesstron.data.model.PieceColor


@Composable
fun ChessPieceView(
    piece: ChessPiece,
    cellSize: Dp,
    isSelected: Boolean,
    isAttackTarget: Boolean,
    playerColor: PieceColor
) {

    val targetX = if (playerColor == PieceColor.WHITE) piece.col * cellSize else (7 - piece.col) * cellSize
    val targetY = if (playerColor == PieceColor.WHITE) piece.row * cellSize else (7 - piece.row) * cellSize


    val animatedX by animateDpAsState(
        targetValue = targetX,
        animationSpec = tween(durationMillis = 300),
        label = "AnimatedX"
    )

    val animatedY by animateDpAsState(
        targetValue = targetY,
        animationSpec = tween(durationMillis = 300),
        label = "AnimatedY"
    )

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.15f else 1f,
        animationSpec = tween(durationMillis = 200),
        label = "PieceScale"
    )

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    animatedX.roundToPx(),
                    animatedY.roundToPx()
                )
            }
            .size(cellSize)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                translationY = if (isSelected) -8f else 0f
            }
    ) {
        if (isAttackTarget) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp)
                    .background(Color(0x33FF0000), shape = RoundedCornerShape(50))
                    .align(Alignment.Center)
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp)
                    .background(Color(0x66FF0000), shape = RoundedCornerShape(50))
                    .align(Alignment.Center)
            )
        }

        Image(
            painter = painterResource(id = getDrawableForPiece(piece)),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
    }
}

