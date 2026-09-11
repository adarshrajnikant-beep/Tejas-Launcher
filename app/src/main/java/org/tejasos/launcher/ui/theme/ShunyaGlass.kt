package org.tejasos.launcher.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.shunyaGlass(
    cornerRadius: Dp = 24.dp,
    blurRadius: Float = 0f,
    surfaceAlpha: Float = 0.55f
): Modifier {
    val shape = RoundedCornerShape(cornerRadius)
    return this
        .clip(shape)
        .background(
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF232A38).copy(alpha = surfaceAlpha),
                    Color(0xFF101520).copy(alpha = surfaceAlpha + 0.15f)
                )
            )
        )
        .border(
            width = 1.dp,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.28f),
                    Color.White.copy(alpha = 0.06f)
                )
            ),
            shape = shape
        )
}
