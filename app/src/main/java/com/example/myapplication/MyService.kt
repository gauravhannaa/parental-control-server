package com.example.myapplication

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.util.Log

class MyService : AccessibilityService() {

    private var lastApp = ""
    private var lastTime = 0L

    override fun onServiceConnected() {
        super.onServiceConnected()

        Log.d("SERVICE", "✅ Service Started")

        // 🔥 TEST API
        ApiHelper.sendLogs("TEST_APP", "123", "5s")

        // ✅ LOCATION START (helper se call karo)
        startLocationUpdates(this)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

        if (event == null) return

        val packageName = event.packageName?.toString() ?: return

        // 🔥 same app ignore
        if (packageName == lastApp) return

        val currentTime = System.currentTimeMillis()

        val duration = if (lastTime != 0L) {
            ((currentTime - lastTime) / 1000).toString() + " sec"
        } else {
            "0 sec"
        }

        // 🔥 previous app send
        if (lastApp.isNotEmpty()) {
            ApiHelper.sendLogs(lastApp, lastTime.toString(), duration)
        }

        // 🔥 update current
        lastApp = packageName
        lastTime = currentTime

        Log.d("TRACK", "📱 Current App: $packageName")
    }

    override fun onInterrupt() {
        Log.d("SERVICE", "❌ Interrupted")
    }
}