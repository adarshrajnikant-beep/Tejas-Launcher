package org.tejasos.launcher.ui.home

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import org.tejasos.launcher.security.kavach.KavachRadarWidget
import org.tejasos.launcher.security.vault.GuptVaultOverlay
import org.tejasos.launcher.ui.controlcenter.TejasControlCenter
import org.tejasos.launcher.ui.dock.TejasFloatingDock
import org.tejasos.launcher.ui.island.CapsuleState
import org.tejasos.launcher.ui.island.TejasCapsule
import org.tejasos.launcher.ui.library.AppLibraryView
import org.tejasos.launcher.ui.lock.TejasLockScreen
import org.tejasos.launcher.ui.search.TejasKhojOverlay
import org.tejasos.launcher.ui.theme.TejasHaptics
import org.tejasos.launcher.ui.theme.TejasKesari
import org.tejasos.launcher.ui.theme.shunyaGlass
import org.tejasos.launcher.ui.theme.tejasParallax
import org.tejasos.launcher.ui.widgets.bharat.NavicWeatherWidget
import org.tejasos.launcher.ui.widgets.bharat.SanskritiPanchangWidget
import org.tejasos.launcher.ui.widgets.bharat.TejasQuickPayBar
import org.tejasos.launcher.viewmodel.AppModel
import org.tejasos.launcher.viewmodel.LauncherViewModel

@Composable
fun HomeScreen(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val allApps by viewModel.installedApps.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // सभी इंटरएक्टिव स्टेट्स
    var isControlCenterOpen by remember { mutableStateOf(false) }
    var isKhojOpen by remember { mutableStateOf(false) }
    var isVaultOpen by remember { mutableStateOf(false) }
    var isLibraryOpen by remember { mutableStateOf(false) }
    var isLockScreenOpen by remember { mutableStateOf(false) }

    // गुप्त वॉल्ट पैकेज सेटिंग्स
    var vaultPackages by remember { mutableStateOf(setOf<String>()) }

    val visibleApps = remember(allApps, vaultPackages) {
        allApps.filter { it.packageName !in vaultPackages }
    }
    val hiddenVaultApps = remember(allApps, vaultPackages) {
        allApps.filter { it.packageName in vaultPackages }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            // 🌟 फ्रॉस्टेड ग्लास बैकड्रॉप: यह पारदर्शी ग्रेडिएंट सिस्टम वॉलपेपर को धुंधला करके दिखाता है
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0x880A0E17),
                        Color(0x99111726),
                        Color(0xB3070A10)
                    )
                )
            )
            // 🔒 खाली जगह पर डबल टैप करने पर: फोन की तेजस लॉक स्क्रीन सक्रिय होगी
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        TejasHaptics.heavyClick(context)
                        isLockScreenOpen = true
                    }
                )
            }
            // 👆 मास्टर जेस्चर डिटेक्टर (कंट्रोल सेंटर, खोज, ऐप लाइब्रेरी)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    // ऊपर दाईं ओर से नीचे: तेजस कंट्रोल सेंटर
                    if (change.position.y < 380 && change.position.x > size.width * 0.55f && dragAmount.y > 30) {
                        TejasHaptics.tick(context)
                        isControlCenterOpen = true
                    }
                    // स्क्रीन के बीच से नीचे: तेजस खोज (Spotlight Search + Calculator)
                    else if (change.position.y in 220f..800f && dragAmount.y > 35 && change.position.x in (size.width * 0.15f)..(size.width * 0.85f)) {
                        TejasHaptics.tick(context)
                        isKhojOpen = true
                    }
                    // नीचे से ऊपर स्वाइप: स्मार्ट ऐप लाइब्रेरी
                    else if (change.position.y > size.height * 0.70f && dragAmount.y < -40) {
                        TejasHaptics.heavyClick(context)
                        isLibraryOpen = true
                    }
                }
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // 1. डायनामिक आइलैंड (Tejas Capsule)
            TejasCapsule(
                currentState = CapsuleState.MEDIA_ACTIVE,
                onUpiClick = { viewModel.launchUpiScanner() }
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = TejasKesari)
                }
            } else {
                // 2. मुख्य डेस्कटॉप ग्रिड (3D पैरालैक्स विजेट्स + ऐप्स)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 115.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // विजेट 1: तेजस 1-टैप क्विक पे बार
                    item(span = { GridItemSpan(4) }) {
                        TejasQuickPayBar(
                            modifier = Modifier.tejasParallax(0.5f),
                            onScanQrClick = { viewModel.launchUpiScanner() },
                            onDirectUpiClick = { isKhojOpen = true }
                        )
                    }

                    // विजेट 2: राष्ट्रीय संस्कृति व पंचांग विजेट
                    item(span = { GridItemSpan(4) }) {
                        SanskritiPanchangWidget(
                            modifier = Modifier.tejasParallax(0.8f)
                        )
                    }

                    // विजेट 3: इसरो NavIC सैटेलाइट व मौसम हब
                    item(span = { GridItemSpan(4) }) {
                        NavicWeatherWidget(
                            modifier = Modifier.tejasParallax(0.8f)
                        )
                    }

                    // विजेट 4: कवच लाइव प्राइवेसी रडार HUD
                    item(span = { GridItemSpan(4) }) {
                        KavachRadarWidget(
                            modifier = Modifier.tejasParallax(0.6f)
                        )
                    }

                    // हेडर: अनुप्रयोग व गुप्त वॉल्ट ट्रिगर
                    item(span = { GridItemSpan(4) }) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "अनुप्रयोग (Apps)",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "गुप्त वॉल्ट 🔒",
                                color = TejasKesari,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.clickable {
                                    TejasHaptics.heavyClick(context)
                                    isVaultOpen = true
                                }
                            )
                        }
                    }

                    // 3. ऐप आइकन्स (लॉन्ग प्रेस मेनू + iOS रेड नोटिफिकेशन बैज)
                    items(visibleApps, key = { it.packageName }) { app ->
                        AppGridItem(
                            app = app,
                            onClick = {
                                TejasHaptics.tick(context)
                                viewModel.launchApp(app.packageName)
                            },
                            onHideToVault = { pkg ->
                                vaultPackages = vaultPackages + pkg
                            }
                        )
                    }
                }
            }
        }

        // 4. फ्लोटिंग स्मार्ट ग्लास डॉक (Floating Glass Dock)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            contentAlignment = Alignment.BottomCenter
        ) {
            TejasFloatingDock(
                onPhoneClick = {
                    val dialIntent = Intent(Intent.ACTION_DIAL).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
                    try { context.startActivity(dialIntent) } catch (e: Exception) {}
                },
                onMessageClick = {
                    val smsIntent = Intent(Intent.ACTION_MAIN).apply {
                        addCategory(Intent.CATEGORY_APP_MESSAGING)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    try { context.startActivity(smsIntent) } catch (e: Exception) {
                        viewModel.launchApp("com.google.android.apps.messaging")
                    }
                },
                onUpiClick = { viewModel.launchUpiScanner() },
                onBrowserClick = { isKhojOpen = true },
                onLibraryClick = { isLibraryOpen = true }
            )
        }

        // 5. स्मार्ट ऐप लाइब्रेरी ओवरले (2x2 Categorized Folders)
        AnimatedVisibility(
            visible = isLibraryOpen,
            enter = fadeIn() + slideInVertically { it },
            exit = fadeOut() + slideOutVertically { it }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.92f))
                    .statusBarsPadding()
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "स्मार्ट ऐप लाइब्रेरी",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f))
                                .clickable { isLibraryOpen = false },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Rounded.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                    AppLibraryView(
                        allApps = allApps,
                        onAppClick = { pkg ->
                            isLibraryOpen = false
                            viewModel.launchApp(pkg)
                        }
                    )
                }
            }
        }

        // 6. तेजस खोज ओवरले (Spotlight Search + Calculator)
        TejasKhojOverlay(
            isOpen = isKhojOpen,
            apps = allApps,
            onDismiss = { isKhojOpen = false },
            onAppClick = { pkg ->
                isKhojOpen = false
                viewModel.launchApp(pkg)
            }
        )

        // 7. तेजस कंट्रोल सेंटर ओवरले
        TejasControlCenter(
            isOpen = isControlCenterOpen,
            onDismiss = { isControlCenterOpen = false },
            onUpiScanLaunch = {
                isControlCenterOpen = false
                viewModel.launchUpiScanner()
            },
            onDigiLockerLaunch = {
                isControlCenterOpen = false
                viewModel.launchDigiLocker()
            }
        )

        // 8. तेजस गुप्त वॉल्ट ओवरले
        GuptVaultOverlay(
            isOpen = isVaultOpen,
            vaultApps = hiddenVaultApps,
            onDismiss = { isVaultOpen = false },
            onAppClick = { pkg ->
                isVaultOpen = false
                viewModel.launchApp(pkg)
            }
        )

        // 9. तेजस लॉक स्क्रीन व सुभाषित इंजन (Lockscreen Overlay)
        TejasLockScreen(
            isLocked = isLockScreenOpen,
            onUnlock = { isLockScreenOpen = false },
            onLaunchCamera = {
                val camIntent = Intent("android.media.action.STILL_IMAGE_CAMERA").apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                try { context.startActivity(camIntent) } catch (e: Exception) {}
            }
        )
    }
}

/**
 * स्क्वर्ल ऐप आइकन, नोटिफिकेशन बैज और लॉन्ग-प्रेस संदर्भ मेनू
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppGridItem(
    app: AppModel,
    onClick: () -> Unit,
    onHideToVault: (String) -> Unit
) {
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onClick() },
                onLongClick = {
                    TejasHaptics.heavyClick(context)
                    showMenu = true
                }
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(58.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = app.icon),
                contentDescription = app.name,
                modifier = Modifier.size(46.dp)
            )

            // 🔴 iOS स्टाइल नोटिफिकेशन बैज (Red Dot Badge)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(9.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF3B30))
            )

            // लॉन्ग-प्रेस करने पर ड्रॉपडाउन मेनू
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                modifier = Modifier
                    .background(Color(0xFF1E2430))
                    .clip(RoundedCornerShape(16.dp))
            ) {
                DropdownMenuItem(
                    text = { Text("ऐप जानकारी (Info)", color = Color.White, fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Rounded.Info, contentDescription = null, tint = Color.White) },
                    onClick = {
                        showMenu = false
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.parse("package:${app.packageName}")
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(intent)
                    }
                )
                DropdownMenuItem(
                    text = { Text("वॉल्ट में छुपाएँ (Hide)", color = TejasKesari, fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Rounded.Lock, contentDescription = null, tint = TejasKesari) },
                    onClick = {
                        showMenu = false
                        TejasHaptics.heavyClick(context)
                        onHideToVault(app.packageName)
                    }
                )
                DropdownMenuItem(
                    text = { Text("अनइंस्टॉल करें", color = Color(0xFFFF5252), fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Rounded.Delete, contentDescription = null, tint = Color(0xFFFF5252)) },
                    onClick = {
                        showMenu = false
                        val uninstallIntent = Intent(Intent.ACTION_DELETE).apply {
                            data = Uri.parse("package:${app.packageName}")
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(uninstallIntent)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = app.name,
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}