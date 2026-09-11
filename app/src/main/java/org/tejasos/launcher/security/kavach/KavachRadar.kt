package org.tejasos.launcher.security.kavach

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.tejasos.launcher.ui.theme.shunyaGlass

@Composable
fun KavachRadarWidget(
    modifier: Modifier = Modifier,
    isCameraSafe: Boolean = true,
    isMicSafe: Boolean = true,
    isLocationSafe: Boolean = true
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(86.dp)
            .shunyaGlass(cornerRadius = 24.dp)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF00C853).copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Shield,
                        contentDescription = "Kavach Protection",
                        tint = Color(0xFF00E676),
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "कवच रडार • सुरक्षित",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "कोई गुप्त निगरानी सक्रिय नहीं",
                        color = Color.White.copy(alpha = 0.55f),
                        fontSize = 11.sp
                    )
                }
            }

            // हार्डवेयर सेंसर लाइव इंडिकेटर्स
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SensorIndicator(Icons.Rounded.CameraAlt, isCameraSafe)
                SensorIndicator(Icons.Rounded.Mic, isMicSafe)
                SensorIndicator(Icons.Rounded.MyLocation, isLocationSafe)
            }
        }
    }
}

@Composable
fun SensorIndicator(icon: ImageVector, isSafe: Boolean) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(
                if (isSafe) Color.White.copy(alpha = 0.1f)
                else Color(0xFFFF5252).copy(alpha = 0.25f)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSafe) Color.White.copy(alpha = 0.7f) else Color(0xFFFF5252),
            modifier = Modifier.size(17.dp)
        )
    }
}
