package com.example.chesstron.domain.usecase

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChessCell(
    row: Int,
    col: Int,
    cellSize: Dp,
    onClick: () -> Unit,
    isHighlighted: Boolean = false,
    isInCheck: Boolean = false,
    isCheckmate: Boolean = false
){
    var isPressed by remember { mutableStateOf(false) }

    val isLight = (row + col) % 2 == 0
    val baseColor = if (isLight) Color(0xFFF0D9B5) else Color(0xFFB58863)
    val pressedColor = if (isLight) Color(0xFFE8C785) else Color(0xFF9C6C48)

    val animatedColor by animateColorAsState(
        targetValue = if (isPressed) pressedColor else baseColor,
        animationSpec = tween(durationMillis = 400),
        label = "CellColor"
    )
    val coordColor = Color(0xFF1A1A1A)

    val dangerColor by animateColorAsState(
        targetValue = when {
            isCheckmate -> Color(0xFFFF0000) // постійно червона
            isInCheck -> Color(0x66FF0000) // напівпрозора
            else -> animatedColor
        },
        animationSpec = tween(durationMillis = 600),
        label = "DangerColor"
    )
    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(300L)
            isPressed = false
        }
    }

    Box(
        modifier = Modifier
            .size(cellSize)
            .background(dangerColor)
            .clickable {
                isPressed = true
                onClick()
            }


    ) {
        if (col == 0) {
            Text(
                text = "${8 - row}",
                color = coordColor,
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(6.dp)
            )
        }

        if (row == 7) {
            Text(
                text = ('a' + col).toString(),
                color = coordColor,
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(6.dp)
            )
        }

        if (isHighlighted) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .background(Color(0x8832CD32), shape = RoundedCornerShape(50))
                    .align(Alignment.Center)
            )
        }
    }

}
