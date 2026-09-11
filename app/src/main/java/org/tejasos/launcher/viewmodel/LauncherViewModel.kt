package org.tejasos.launcher.viewmodel

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.LauncherApps
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Process
import android.os.UserManager
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class AppModel(
    val name: String,
    val packageName: String,
    val icon: Drawable
)

class LauncherViewModel(application: Application) : AndroidViewModel(application) {

    private val _installedApps = MutableStateFlow<List<AppModel>>(emptyList())
    val installedApps: StateFlow<List<AppModel>> = _installedApps.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val packageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            loadInstalledApps()
        }
    }

    private var isReceiverRegistered = false

    init {
        loadInstalledApps()
        registerPackageReceiver()
    }

    fun loadInstalledApps() {
        viewModelScope.launch {
            _isLoading.value = true
            val appsList = withContext(Dispatchers.IO) {
                val result = mutableListOf<AppModel>()
                val app = getApplication<Application>()

                try {
                    val launcherApps = app.getSystemService(Context.LAUNCHER_APPS_SERVICE) as? LauncherApps
                    val userManager = app.getSystemService(Context.USER_SERVICE) as? UserManager
                    val profiles = userManager?.userProfiles ?: listOf(Process.myUserHandle())

                    if (launcherApps != null) {
                        for (profile in profiles) {
                            val activityList = launcherApps.getActivityList(null, profile)
                            for (act in activityList) {
                                if (act.applicationInfo.packageName != app.packageName) {
                                    result.add(
                                        AppModel(
                                            name = act.label.toString(),
                                            packageName = act.applicationInfo.packageName,
                                            icon = act.getBadgedIcon(0)
                                        )
                                    )
                                }
                            }
                        }
                    }
                } catch (e: Exception) {}

                if (result.isEmpty()) {
                    try {
                        val pm = app.packageManager
                        val mainIntent = Intent(Intent.ACTION_MAIN).apply {
                            addCategory(Intent.CATEGORY_LAUNCHER)
                        }
                        val resolveInfos = pm.queryIntentActivities(mainIntent, 0)
                        for (info in resolveInfos) {
                            if (info.activityInfo.packageName != app.packageName) {
                                result.add(
                                    AppModel(
                                        name = info.loadLabel(pm).toString(),
                                        packageName = info.activityInfo.packageName,
                                        icon = info.loadIcon(pm)
                                    )
                                }
                            }
                        }
                    } catch (e: Exception) {}
                }

                result.distinctBy { it.packageName }.sortedBy { it.name.lowercase() }
            }
            _installedApps.value = appsList
            _isLoading.value = false
        }
    }

    private fun registerPackageReceiver() {
        if (isReceiverRegistered) return
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addAction(Intent.ACTION_PACKAGE_CHANGED)
            addDataScheme("package")
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                getApplication<Application>().registerReceiver(
                    packageReceiver,
                    filter,
                    Context.RECEIVER_EXPORTED
                )
            } else {
                getApplication<Application>().registerReceiver(packageReceiver, filter)
            }
            isReceiverRegistered = true
        } catch (e: Exception) {}
    }

    fun launchApp(packageName: String) {
        try {
            val pm = getApplication<Application>().packageManager
            val launchIntent = pm.getLaunchIntentForPackage(packageName)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                getApplication<Application>().startActivity(launchIntent)
            } else {
                Toast.makeText(getApplication(), "अनुप्रयोग खोलने में असमर्थ", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(getApplication(), "त्रुटि: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    fun launchUpiScanner() {
        val context = getApplication<Application>()
        val pm = context.packageManager
        val upiIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("upi://pay")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        if (upiIntent.resolveActivity(pm) != null) {
            try {
                context.startActivity(upiIntent)
                return
            } catch (e: Exception) {}
        }

        val popularUpiApps = listOf(
            "in.org.npci.upiapp",
            "com.phonepe.app",
            "net.one97.paytm",
            "com.google.android.apps.nbu.paisa.user"
        )

        for (pkg in popularUpiApps) {
            val appIntent = pm.getLaunchIntentForPackage(pkg)
            if (appIntent != null) {
                appIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                try {
                    context.startActivity(appIntent)
                    return
                } catch (e: Exception) {}
            }
        }

        val cameraIntent = Intent("android.media.action.STILL_IMAGE_CAMERA").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(cameraIntent)
        } catch (ex: Exception) {
            Toast.makeText(context, "कोई UPI स्कैनर उपलब्ध नहीं है", Toast.LENGTH_SHORT).show()
        }
    }

    fun launchDigiLocker() {
        val context = getApplication<Application>()
        val pm = context.packageManager
        val digiIntent = pm.getLaunchIntentForPackage("com.digilocker.android")

        if (digiIntent != null) {
            digiIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(digiIntent)
        } else {
            val webIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.digilocker.gov.in/")
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                context.startActivity(webIntent)
            } catch (e: Exception) {
                Toast.makeText(context, "डिजिलॉकर खोलने में असमर्थ", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (isReceiverRegistered) {
            try {
                getApplication<Application>().unregisterReceiver(packageReceiver)
                isReceiverRegistered = false
            } catch (e: Exception) {}
        }
    }
}
