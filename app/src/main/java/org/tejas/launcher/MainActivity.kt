package org.tejas.launcher

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.tejas.launcher.ui.screens.IndiaWidgetsView
import org.tejas.launcher.ui.screens.TejasControlCenter
import org.tejas.launcher.ui.theme.BackgroundGradient

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TejasHomeWorkspace()
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TejasHomeWorkspace() {
    val context = LocalContext.current
    val pagerState = rememberPagerState(initialPage = 1, pageCount = { 2 })
    var isControlCenterOpen by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGradient)
            .pointerInput(Unit) {
                // टॉप राइट से नीचे स्वाइप करने पर कंट्रोल सेंटर खुलेगा
                detectDragGestures { change, dragAmount ->
                    if (change.position.y < 200 && change.position.x > size.width * 0.6f && dragAmount.y > 30) {
                        isControlCenterOpen = true
                    }
                }
            }
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            if (page == 0) {
                IndiaWidgetsView()
            } else {
                HomeAppsGrid()
            }
        }

        // स्वाइप-डाउन iOS स्टाइल कंट्रोल सेंटर
        TejasControlCenter(
            isOpen = isControlCenterOpen,
            onClose = { isControlCenterOpen = false }
        )
    }
}

@Composable
fun HomeAppsGrid() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 80.dp, start = 24.dp, end = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // क्लॉक विजेट
        Text("09:41", fontSize = 72.sp, color = Color.White, style = MaterialTheme.typography.displayLarge)
        Text("तेजस फोन 1 • आज़ाद", fontSize = 13.sp, color = Color.LightGray)

        Spacer(modifier = Modifier.weight(1f))

        // डॉक बार (iOS स्टाइल फ्लोटिंग स्क्वर्ल डॉक)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
                .height(88.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(Color(0x33FFFFFF)),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DockIcon(label = "फ़ोन", color = Color(0xFF34C759))
                DockIcon(label = "संदेश", color = Color(0xFF007AFF))
                DockIcon(label = "तेजस ब्राउज़र", color = Color(0xFFFF9500))
                DockIcon(label = "स्टोर", color = Color(0xFFAF52DE))
            }
        }
    }
}

@Composable
fun DockIcon(label: String, color: Color) {
    Box(
        modifier = Modifier
            .size(54.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(label.take(1), color = Color.White, fontSize = 20.sp)
    }
}

