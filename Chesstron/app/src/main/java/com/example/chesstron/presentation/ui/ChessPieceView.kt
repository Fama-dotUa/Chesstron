package com.example.chesstron.presentation.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    isSelected: Boolean,
    isAttackTarget: Boolean
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
                scaleX = if (isSelected) 1.15f else 1f
                scaleY = if (isSelected) 1.15f else 1f
                translationY = if (isSelected) -8f else 0f
            }
    ) {
        if (isAttackTarget) {
            // Розмите світіння під фігурою
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp)
                    .background(Color(0x33FF0000), shape = RoundedCornerShape(50)) // зовнішній прозорий ореол
                    .align(Alignment.Center)
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp)
                    .background(Color(0x66FF0000), shape = RoundedCornerShape(50)) // внутрішній концентрований шар
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

