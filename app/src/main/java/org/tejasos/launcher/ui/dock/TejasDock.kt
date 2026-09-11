package org.tejasos.launcher.ui.dock

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AllInclusive
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.ChatBubble
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import org.tejasos.launcher.ui.theme.TejasHaptics
import org.tejasos.launcher.ui.theme.TejasKesari
import org.tejasos.launcher.ui.theme.shunyaGlass

@Composable
fun TejasFloatingDock(
    modifier: Modifier = Modifier,
    onPhoneClick: () -> Unit,
    onMessageClick: () -> Unit,
    onBrowserClick: () -> Unit,
    onUpiClick: () -> Unit,
    onLibraryClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        // तैरता हुआ फ्रॉस्टेड ग्लास डॉक
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(84.dp)
                .shunyaGlass(cornerRadius = 38.dp, blurRadius = 60f, surfaceAlpha = 0.55f)
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DockIconItem(
                    icon = Icons.Rounded.Phone,
                    backgroundColor = Color(0xFF00C853),
                    onClick = onPhoneClick
                )
                DockIconItem(
                    icon = Icons.Rounded.ChatBubble,
                    backgroundColor = Color(0xFF2979FF),
                    onClick = onMessageClick
                )
                // बीच का मुख्य स्वदेशी बटन: तेजस पे (UPI स्कैनर)
                DockIconItem(
                    icon = Icons.Rounded.QrCodeScanner,
                    backgroundColor = TejasKesari,
                    isCenterFeatured = true,
                    onClick = onUpiClick
                )
                DockIconItem(
                    icon = Icons.Rounded.Language,
                    backgroundColor = Color(0xFFFF9100),
                    onClick = onBrowserClick
                )
                DockIconItem(
                    icon = Icons.Rounded.AllInclusive,
                    backgroundColor = Color(0xFF651FFF),
                    onClick = onLibraryClick
                )
            }
        }
    }
}

@Composable
fun DockIconItem(
    icon: ImageVector,
    backgroundColor: Color,
    isCenterFeatured: Boolean = false,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.82f else if (isCenterFeatured) 1.1f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "DockSpring"
    )

    Box(
        modifier = Modifier
            .size(if (isCenterFeatured) 62.dp else 54.dp)
            .scale(scale)
            .clip(RoundedCornerShape(if (isCenterFeatured) 22.dp else 18.dp))
            .background(backgroundColor)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        TejasHaptics.tick(context)
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = {
                        TejasHaptics.heavyClick(context)
                        onClick()
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(if (isCenterFeatured) 30.dp else 26.dp)
        )
    }
}
