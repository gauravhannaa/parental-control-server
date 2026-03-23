package com.example.myapplication

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔥 NEW: Send dummy log (IMPORTANT FIX)
        sendAppLog()

        // 🔥 OLD (kept)
        sendLocation(this)

        // ✅ Permission check
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )

        } else {
            // ✅ Permission already granted
            sendLocation(this)
        }

        setContent {
            DashboardScreen()
        }
    }

    // =========================
    // 🔥 NEW FUNCTION (LOG SEND)
    // =========================
    private fun sendAppLog() {

        val appName = "My App"
        val time = System.currentTimeMillis().toString()
        val duration = "5 sec"

        ApiHelper.sendLogs(appName, time, duration)
    }

    // =========================
    // ✅ Permission result handle
    // =========================
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                // ✅ Permission granted
                sendLocation(this)

            } else {
                // ❌ Permission denied
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // =========================
    // 📍 LOCATION FUNCTION (UPGRADED)
    // =========================
    fun sendLocation(context: Context) {

        Toast.makeText(context, "Location Access Granted ✅", Toast.LENGTH_SHORT).show()

        // 🔥 NEW: Dummy location send (IMPORTANT FIX)
        val lat = 28.6139
        val lng = 77.2090

        ApiHelper.sendLocation(lat, lng)
    }
}