package org.tejasos.launcher.ui.theme

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Indian Flag & Minimalist Dark Palette Accents
val TejasKesari = Color(0xFFFF671F)
val TejasWhite = Color(0xFFFFFFFF)
val TejasAshoka = Color(0xFF06038D)
val GlassSurfaceDark = Color(0x33121212)
val GlassBorderLight = Color(0x40FFFFFF)

/**
 * Shunya UI Glassmorphism Modifier
 * Replicates iOS frosted glass physics safely using clean Jetpack Compose RenderEffects.
 */
fun Modifier.shunyaGlass(
    cornerRadius: Dp = 24.dp,
    blurRadius: Float = 45f,
    surfaceAlpha: Float = 0.45f,
    borderAlpha: Float = 0.25f
): Modifier = this
    .clip(RoundedCornerShape(cornerRadius))
    .graphicsLayer {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            renderEffect = RenderEffect
                .createBlurEffect(blurRadius, blurRadius, Shader.TileMode.CLAMP)
                .asComposeRenderEffect()
        }
    }
    .background(
        Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = surfaceAlpha * 0.35f),
                Color.Black.copy(alpha = surfaceAlpha)
            )
        )
    )
    .border(
        width = 1.dp,
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = borderAlpha),
                Color.White.copy(alpha = borderAlpha * 0.1f)
            )
        ),
        shape = RoundedCornerShape(cornerRadius)
    )
