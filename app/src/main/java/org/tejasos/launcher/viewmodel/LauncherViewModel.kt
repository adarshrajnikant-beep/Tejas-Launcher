package org.tejasos.launcher.viewmodel

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.core.content.ContextCompat
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

    // रीयल-टाइम पैकेज रिसीवर: नया ऐप इंस्टॉल/अनइंस्टॉल होते ही स्क्रीन अपडेट होगी
    private val packageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (action == Intent.ACTION_PACKAGE_ADDED ||
                action == Intent.ACTION_PACKAGE_REMOVED ||
                action == Intent.ACTION_PACKAGE_REPLACED ||
                action == Intent.ACTION_PACKAGE_CHANGED
            ) {
                loadInstalledApps()
            }
        }
    }

    private var isReceiverRegistered = false

    init {
        loadInstalledApps()
        registerPackageReceiver()
    }

    /**
     * सिस्टम के सभी ऐप्स को बैकग्राउंड थ्रेड में स्कैन करके लोड करना
     */
    fun loadInstalledApps() {
        viewModelScope.launch {
            _isLoading.value = true
            val appsList = withContext(Dispatchers.IO) {
                try {
                    val pm = getApplication<Application>().packageManager
                    val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
                        addCategory(Intent.CATEGORY_LAUNCHER)
                    }

                    val resolveInfos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        pm.queryIntentActivities(mainIntent, PackageManager.ResolveInfoFlags.of(0L))
                    } else {
                        @Suppress("DEPRECATION")
                        pm.queryIntentActivities(mainIntent, 0)
                    }

                    resolveInfos
                        .filter { it.activityInfo.packageName != getApplication<Application>().packageName }
                        .map { resolveInfo ->
                            AppModel(
                                name = resolveInfo.loadLabel(pm).toString(),
                                packageName = resolveInfo.activityInfo.packageName,
                                icon = resolveInfo.loadIcon(pm)
                            )
                        }
                        .sortedBy { it.name.lowercase() }
                } catch (e: Exception) {
                    emptyList()
                }
            }
            _installedApps.value = appsList
            _isLoading.value = false
        }
    }

    /**
     * ऑटो-रिफ्रेश रिसीवर रजिस्टर करना (Android 14+ सुरक्षा अनुपालन के साथ)
     */
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
        } catch (e: Exception) {
            // बैकग्राउंड रजिस्ट्रेशन एरर प्रोटेक्शन
        }
    }

    /**
     * ऐप लॉन्च इंजन (क्रैश-प्रूफ ट्राई-कैच)
     */
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

    /**
     * स्वदेशी भारत UPI इंजन: 1-टैप में सीधे कैमरा स्कैनर या किसी भी प्रमुख भारतीय UPI ऐप को खोलना
     */
    fun launchUpiScanner() {
        val context = getApplication<Application>()
        val pm = context.packageManager

        // प्राथमिकता 1: जेनेरिक UPI पे Intent
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

        // प्राथमिकता 2: भारतीय UPI ऐप्स (BHIM, PhonePe, Paytm, GooglePay) की सीधी जांच
        val popularUpiApps = listOf(
            "in.org.npci.upiapp",                  // BHIM UPI (National)
            "com.phonepe.app",                     // PhonePe
            "net.one97.paytm",                     // Paytm
            "com.google.android.apps.nbu.paisa.user" // Google Pay India
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

        // प्राथमिकता 3: सीधे हार्डवेयर कैमरा स्कैनर
        val cameraIntent = Intent("android.media.action.STILL_IMAGE_CAMERA").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(cameraIntent)
        } catch (ex: Exception) {
            Toast.makeText(context, "कोई UPI स्कैनर उपलब्ध नहीं है", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * डिजिलॉकर वॉल्ट ट्रिगर (ऐप न होने पर वेब पोर्टल का सुरक्षित फॉलबैक)
     */
    fun launchDigiLocker() {
        val context = getApplication<Application>()
        val pm = context.packageManager
        val digiIntent = pm.getLaunchIntentForPackage("com.digilocker.android")

        if (digiIntent != null) {
            digiIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(digiIntent)
        } else {
            // डिजिलॉकर ऐप न होने पर सीधे भारत सरकार के आधिकारिक वेब पोर्टल पर ले जाना
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

    /**
     * मेमोरी लीक से बचाव: व्यू-मॉडल नष्ट होने पर ब्रॉडकास्ट को सुरक्षित रूप से अनरजिस्टर करना
     */
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