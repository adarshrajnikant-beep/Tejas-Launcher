package org.tejasos.launcher.ui.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import org.tejasos.launcher.ui.theme.TejasKesari
import org.tejasos.launcher.ui.theme.shunyaGlass
import org.tejasos.launcher.viewmodel.AppModel

@Composable
fun TejasKhojOverlay(
    isOpen: Boolean,
    apps: List<AppModel>,
    onDismiss: () -> Unit,
    onAppClick: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    // तत्काल मैथ कैलकुलेटर इंजन
    val mathResult = remember(searchQuery) {
        evaluateSimpleMath(searchQuery)
    }

    // ऐप फिल्टरिंग
    val filteredApps = remember(searchQuery, apps) {
        if (searchQuery.isBlank()) emptyList()
        else apps.filter { it.name.contains(searchQuery, ignoreCase = true) }.take(6)
    }

    AnimatedVisibility(
        visible = isOpen,
        enter = fadeIn() + slideInVertically { -it / 3 },
        exit = fadeOut() + slideOutVertically { -it / 3 }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.75f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    searchQuery = ""
                    onDismiss()
                },
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 60.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { /* कार्ड टच पर बंद न हो */ },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // फ्रॉस्टेड सर्च बार
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .shunyaGlass(cornerRadius = 30.dp)
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = "Search",
                            tint = TejasKesari,
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            singleLine = true,
                            textStyle = TextStyle(
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            cursorBrush = SolidColor(TejasKesari),
                            modifier = Modifier.weight(1f),
                            decorationBox = { innerTextField ->
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        text = "तेजस खोज: ऐप्स, गणना (जैसे 25*4), वेब...",
                                        color = Color.White.copy(alpha = 0.45f),
                                        fontSize = 14.sp
                                    )
                                }
                                innerTextField()
                            }
                        )

                        if (searchQuery.isNotEmpty()) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = "Clear",
                                tint = Color.White.copy(alpha = 0.6f),
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable { searchQuery = "" }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 1. लाइव मैथ रिजल्ट कार्ड (यदि यूजर हिसाब लिख रहा हो)
                if (mathResult != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shunyaGlass(cornerRadius = 22.dp)
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Rounded.Calculate,
                                    contentDescription = null,
                                    tint = Color(0xFF00E676),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "परिणाम",
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 13.sp
                                )
                            }
                            Text(
                                text = mathResult,
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // 2. ऐप्स परिणाम सूची
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredApps) { app ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                                .shunyaGlass(cornerRadius = 20.dp)
                                .clickable {
                                    onAppClick(app.packageName)
                                    onDismiss()
                                }
                                .padding(horizontal = 14.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = rememberAsyncImagePainter(model = app.icon),
                                    contentDescription = app.name,
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text(
                                        text = app.name,
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "अनुप्रयोग • टैप करके खोलें",
                                        color = Color.White.copy(alpha = 0.5f),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }

                    // 3. वेब सर्च कार्ड (जब क्वेरी लिखी हो)
                    if (searchQuery.isNotBlank()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(58.dp)
                                    .shunyaGlass(cornerRadius = 20.dp)
                                    .clickable { /* वेब सर्च लॉन्च */ }
                                    .padding(horizontal = 14.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Rounded.Language,
                                        contentDescription = null,
                                        tint = TejasKesari,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "\"$searchQuery\" को वेब पर खोजें",
                                        color = Color.White,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// बेसिक इन-लाइन कैलकुलेटर लॉजिक
private fun evaluateSimpleMath(query: String): String? {
    val clean = query.replace(" ", "")
    if (!clean.matches(Regex("^[0-9]+[+\\-*/][0-9]+$"))) return null
    return try {
        when {
            "+" in clean -> {
                val p = clean.split("+")
                (p[0].toDouble() + p[1].toDouble()).toString().removeSuffix(".0")
            }
            "-" in clean -> {
                val p = clean.split("-")
                (p[0].toDouble() - p[1].toDouble()).toString().removeSuffix(".0")
            }
            "*" in clean -> {
                val p = clean.split("*")
                (p[0].toDouble() * p[1].toDouble()).toString().removeSuffix(".0")
            }
            "/" in clean -> {
                val p = clean.split("/")
                if (p[1].toDouble() == 0.0) "अमान्य"
                else String.format("%.2f", p[0].toDouble() / p[1].toDouble()).removeSuffix(".00")
            }
            else -> null
        }
    } catch (e: Exception) {
        null
    }
}
