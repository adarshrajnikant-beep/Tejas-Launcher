package org.tejasos.launcher.ui.controlcenter

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AirplanemodeActive
import androidx.compose.material.icons.rounded.Bluetooth
import androidx.compose.material.icons.rounded.BrightnessHigh
import androidx.compose.material.icons.rounded.FlashlightOn
import androidx.compose.material.icons.rounded.FolderShared
import androidx.compose.material.icons.rounded.NetworkCheck
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.tejasos.launcher.ui.theme.TejasAshoka
import org.tejasos.launcher.ui.theme.TejasKesari
import org.tejasos.launcher.ui.theme.shunyaGlass

@Composable
fun TejasControlCenter(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onUpiScanLaunch: () -> Unit = {},
    onDigiLockerLaunch: () -> Unit = {}
) {
    var volumeLevel by remember { mutableFloatStateOf(0.65f) }
    var brightnessLevel by remember { mutableFloatStateOf(0.80f) }

    var wifiActive by remember { mutableStateOf(true) }
    var btActive by remember { mutableStateOf(true) }
    var dataActive by remember { mutableStateOf(true) }
    var airplaneActive by remember { mutableStateOf(false) }

    var torchActive by remember { mutableStateOf(false) }
    var kavachActive by remember { mutableStateOf(true) }

    AnimatedVisibility(
        visible = isOpen,
        enter = fadeIn() + slideInVertically { -it / 2 },
        exit = fadeOut() + slideOutVertically { -it / 2 }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.65f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onDismiss() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 40.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { /* कार्ड के अंदर टैप होने पर बंद न हो */ },
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Top Row: 2x2 Connectivity Card & Quick Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Connectivity Matrix
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(160.dp)
                            .shunyaGlass(cornerRadius = 28.dp)
                            .padding(14.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                MiniToggle(Icons.Rounded.Wifi, wifiActive) { wifiActive = !wifiActive }
                                MiniToggle(Icons.Rounded.Bluetooth, btActive) { btActive = !btActive }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                MiniToggle(Icons.Rounded.NetworkCheck, dataActive) { dataActive = !dataActive }
                                MiniToggle(Icons.Rounded.AirplanemodeActive, airplaneActive) {
                                    airplaneActive = !airplaneActive
                                }
                            }
                        }
                    }

                    // Tejas Quick UPI Action Card
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(160.dp)
                            .shunyaGlass(cornerRadius = 28.dp)
                            .clickable { onUpiScanLaunch() }
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(TejasKesari),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.QrCodeScanner,
                                    contentDescription = "Scan UPI",
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Tejas Pay",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Tap to Scan QR",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                // Middle Row: Dual Vertical Sliders (Brightness & Volume)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    VerticalPillSlider(
                        modifier = Modifier.weight(1f),
                        value = brightnessLevel,
                        icon = Icons.Rounded.BrightnessHigh,
                        onValueChange = { brightnessLevel = it }
                    )

                    VerticalPillSlider(
                        modifier = Modifier.weight(1f),
                        value = volumeLevel,
                        icon = Icons.Rounded.VolumeUp,
                        onValueChange = { volumeLevel = it }
                    )
                }

                // Bottom Row: Swadeshi Quick Tiles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    TileButton(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Rounded.Security,
                        title = "Kavach",
                        subtitle = if (kavachActive) "Active" else "Off",
                        isActive = kavachActive,
                        activeColor = Color(0xFF00C853)
                    ) { kavachActive = !kavachActive }

                    TileButton(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Rounded.FolderShared,
                        title = "DigiLocker",
                        subtitle = "IDs Ready",
                        isActive = false,
                        activeColor = TejasAshoka
                    ) { onDigiLockerLaunch() }

                    TileButton(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Rounded.FlashlightOn,
                        title = "Torch",
                        subtitle = if (torchActive) "On" else "Off",
                        isActive = torchActive,
                        activeColor = Color(0xFFFFD600)
                    ) { torchActive = !torchActive }
                }
            }
        }
    }
}

@Composable
fun MiniToggle(
    icon: ImageVector,
    active: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(if (active) TejasAshoka else Color.White.copy(alpha = 0.15f))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (active) Color.White else Color.White.copy(alpha = 0.6f),
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun VerticalPillSlider(
    modifier: Modifier = Modifier,
    value: Float,
    icon: ImageVector,
    onValueChange: (Float) -> Unit
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxHeight()
            .shunyaGlass(cornerRadius = 32.dp)
            .pointerInput(Unit) {
                detectVerticalDragGestures { change, _ ->
                    val newRatio = (size.height - change.position.y) / size.height
                    onValueChange(newRatio.coerceIn(0f, 1f))
                }
            }
    ) {
        val fillHeight = maxHeight * value

        // Value Level Fill
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(fillHeight)
                .align(Alignment.BottomCenter)
                .background(Color.White.copy(alpha = 0.35f))
        )

        // Bottom Icon Indicator
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun TileButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    subtitle: String,
    isActive: Boolean,
    activeColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(84.dp)
            .shunyaGlass(cornerRadius = 24.dp)
            .background(if (isActive) activeColor.copy(alpha = 0.25f) else Color.Transparent)
            .clickable { onClick() }
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isActive) activeColor else Color.White,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 10.sp
            )
        }
    }
}
