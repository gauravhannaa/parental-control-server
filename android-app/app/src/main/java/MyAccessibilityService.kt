package com.example.monitorapp

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import okhttp3.*
import org.json.JSONObject

class MyAccessibilityService : AccessibilityService() {

    val client = OkHttpClient()
    val server = "http://10.0.2.2:3000"

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        val pkg = event.packageName?.toString() ?: return

        val req = Request.Builder()
            .url("$server/app-control")
            .build()

        client.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: java.io.IOException) {}

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()

                if (body != null) {
                    val obj = JSONObject(body)
                    val blocked = obj.getJSONArray("blockedApps")

                    for (i in 0 until blocked.length()) {
                        if (pkg == blocked.getString(i)) {
                            performGlobalAction(GLOBAL_ACTION_HOME)
                        }
                    }
                }
            }
        })
    }

    override fun onInterrupt() {}
}