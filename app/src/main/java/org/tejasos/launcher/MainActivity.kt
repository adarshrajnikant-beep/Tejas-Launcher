package org.tejasos.launcher

import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import org.tejasos.launcher.ui.home.HomeScreen
import org.tejasos.launcher.viewmodel.LauncherViewModel

class MainActivity : ComponentActivity() {

    private val launcherViewModel: LauncherViewModel by viewModels()

    // Android 10+ के लिए 1-टैप डिफ़ॉल्ट लॉन्चर प्रॉम्प्ट
    private val roleRequestLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { /* रिस्पॉन्स हैंडलिंग */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ऐप खुलते ही चेक करें कि क्या तेजस डिफ़ॉल्ट है
        checkAndPromptDefaultLauncher()

        setContent {
            HomeScreen(viewModel = launcherViewModel)
        }
    }

    fun checkAndPromptDefaultLauncher() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = getSystemService(Context.ROLE_SERVICE) as? RoleManager
            if (roleManager != null && roleManager.isRoleAvailable(RoleManager.ROLE_HOME)) {
                if (!roleManager.isRoleHeld(RoleManager.ROLE_HOME)) {
                    val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_HOME)
                    roleRequestLauncher.launch(intent)
                }
            }
        } else {
            // Android 9 और पुराने के लिए
            val intent = Intent(Settings.ACTION_HOME_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            try { startActivity(intent) } catch (e: Exception) {}
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // होम लॉन्चर में बैक बटन दबाने से कभी ऐप बंद नहीं होना चाहिए
    }
}