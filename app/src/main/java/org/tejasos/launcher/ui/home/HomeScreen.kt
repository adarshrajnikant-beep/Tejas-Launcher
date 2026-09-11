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

    var isControlCenterOpen by remember { mutableStateOf(false) }
    var isKhojOpen by remember { mutableStateOf(false) }
    var isVaultOpen by remember { mutableStateOf(false) }
    var isLibraryOpen by remember { mutableStateOf(false) }
    var isLockScreenOpen by remember { mutableStateOf(false) }

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
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0x770A0E17),
                        Color(0x88111726),
                        Color(0x99070A10)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // डायनामिक आइलैंड
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        TejasHaptics.tick(context)
                        isKhojOpen = true
                    }
            ) {
                TejasCapsule(
                    currentState = CapsuleState.MEDIA_ACTIVE,
                    onUpiClick = { viewModel.launchUpiScanner() }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // मुख्य डेस्कटॉप विजेट्स और ऐप्स
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item(span = { GridItemSpan(4) }) {
                    TejasQuickPayBar(
                        onScanQrClick = { viewModel.launchUpiScanner() },
                        onDirectUpiClick = { isKhojOpen = true }
                    )
                }

                item(span = { GridItemSpan(4) }) {
                    SanskritiPanchangWidget()
                }

                item(span = { GridItemSpan(4) }) {
                    NavicWeatherWidget()
                }

                item(span = { GridItemSpan(4) }) {
                    KavachRadarWidget()
                }

                item(span = { GridItemSpan(4) }) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "अनुप्रयोग (${visibleApps.size})",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = "कंट्रोल सेंटर ⚙️",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.clickable {
                                    TejasHaptics.tick(context)
                                    isControlCenterOpen = true
                                }
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
                }

                if (isLoading && visibleApps.isEmpty()) {
                    item(span = { GridItemSpan(4) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = TejasKesari, modifier = Modifier.size(32.dp))
                        }
                    }
                } else {
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

        // फ्लोटिंग स्मार्ट डॉक
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

        // ओवरले कंपोनेंट्स
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

        TejasKhojOverlay(
            isOpen = isKhojOpen,
            apps = allApps,
            onDismiss = { isKhojOpen = false },
            onAppClick = { pkg ->
                isKhojOpen = false
                viewModel.launchApp(pkg)
            }
        )

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

        GuptVaultOverlay(
            isOpen = isVaultOpen,
            vaultApps = hiddenVaultApps,
            onDismiss = { isVaultOpen = false },
            onAppClick = { pkg ->
                isVaultOpen = false
                viewModel.launchApp(pkg)
            }
        )

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
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = app.icon),
                contentDescription = app.name,
                modifier = Modifier.size(44.dp)
            )

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
