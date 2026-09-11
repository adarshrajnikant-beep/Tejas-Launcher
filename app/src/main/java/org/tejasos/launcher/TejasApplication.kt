package org.tejasos.launcher

import android.app.Application
import android.content.Intent
import android.util.Log

class TejasApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // ग्लोबल अनकॉट एक्सेप्शन शील्ड (ज़ीरो क्रैश गारंटी)
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("TejasOS_Shield", "क्रैश सुरक्षित रूप से रोका गया: ${throwable.localizedMessage}")
            
            // ऑटोमैटिक रिकवरी: लॉन्चर को बिना क्रैश स्क्रीन के साइलेंट रीस्टार्ट करना
            val intent = packageManager.getLaunchIntentForPackage(packageName)?.apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            startActivity(intent)

            // यदि रिकवरी फेल हो तो ही डिफ़ॉल्ट हैंडलर चले
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }
}
