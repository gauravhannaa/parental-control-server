package com.example.myapplication

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

@Composable
fun DashboardScreen() {

    var appName by remember { mutableStateOf("Loading...") }
    var time by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var locationText by remember { mutableStateOf("Fetching location...") }
    var status by remember { mutableStateOf("Not Connected") }

    val client = OkHttpClient()

    // 🔥 IMPORTANT: yaha apna NEW NGROK URL daalna
    val SERVER = "https://hanna-untransgressed-chieko.ngrok-free.dev"

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.all { it.value }

        CoroutineScope(Dispatchers.Main).launch {
            status = if (granted) "✅ Permissions Granted" else "❌ Permission Denied"
        }
    }

    // 🔥 AUTO FETCH LOOP
    LaunchedEffect(Unit) {
        while (true) {

            // ======================
            // 📊 FETCH LOGS (UPGRADED SAFE)
            // ======================
            val requestLogs = Request.Builder()
                .url("$SERVER/logs")
                .build()

            client.newCall(requestLogs).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    CoroutineScope(Dispatchers.Main).launch {
                        status = "❌ Logs Error: ${e.message}"
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string() ?: ""

                    println("LOGS RESPONSE: $body")

                    try {
                        if (body.isNotEmpty() && body.trim().startsWith("[")) {

                            val arr = JSONArray(body)

                            if (arr.length() > 0) {
                                val obj = arr.optJSONObject(0)

                                if (obj != null) {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        appName = obj.optString("app", "No App")
                                        time = obj.optString("time", "No Time")
                                        duration = obj.optString("duration", "0")
                                        status = "✅ Logs Loaded"
                                    }
                                } else {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        status = "❌ Invalid Object"
                                    }
                                }
                            } else {
                                CoroutineScope(Dispatchers.Main).launch {
                                    status = "⚠️ Empty Logs"
                                }
                            }

                        } else {
                            CoroutineScope(Dispatchers.Main).launch {
                                status = "❌ Invalid Format (Not Array)"
                            }
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                        CoroutineScope(Dispatchers.Main).launch {
                            status = "❌ Parse Safe Error: ${e.message}"
                        }
                    }
                }
            })

            // ======================
            // 📍 FETCH LOCATION (ULTRA SAFE)
            // ======================
            val requestLoc = Request.Builder()
                .url("$SERVER/device")
                .build()

            client.newCall(requestLoc).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    CoroutineScope(Dispatchers.Main).launch {
                        locationText = "❌ Location Error: ${e.message}"
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string() ?: ""

                    println("LOCATION RESPONSE: $body")

                    try {
                        val obj = JSONObject(body)

                        val loc = obj.opt("location")

                        var lat = 0.0
                        var lng = 0.0

                        if (loc is JSONObject && loc.has("lat")) {
                            lat = loc.optDouble("lat", 0.0)
                            lng = loc.optDouble("lon", 0.0)
                        }
                        else if (loc is JSONObject && loc.has("location")) {
                            val inner = loc.optJSONObject("location")
                            if (inner != null) {
                                lat = inner.optDouble("lat", 0.0)
                                lng = inner.optDouble("lon", 0.0)
                            }
                        }

                        CoroutineScope(Dispatchers.Main).launch {
                            locationText = if (lat != 0.0 || lng != 0.0) {
                                "📍 Lat: $lat\n📍 Lng: $lng"
                            } else {
                                "⚠️ No Location Data"
                            }
                        }

                    } catch (e: Exception) {
                        CoroutineScope(Dispatchers.Main).launch {
                            locationText = "❌ Parse Safe Error"
                        }
                    }
                }
            })

            delay(3000)
        }
    }

    // ======================
    // UI
    // ======================
    Column(modifier = Modifier.padding(16.dp)) {

        Text("📊 Live Dashboard", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            permissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            )
        }) {
            Text("🔐 Grant Permissions")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("📱 App: $appName")
        Text("🕒 Time: $time")
        Text("⏱ Duration: $duration")

        Spacer(modifier = Modifier.height(20.dp))

        Text(locationText)

        Spacer(modifier = Modifier.height(20.dp))

        // 🔒 LOCK
        Button(onClick = {
            val json = """{"command":"lock"}"""
            val body = json.toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url("$SERVER/command")
                .post(body)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    CoroutineScope(Dispatchers.Main).launch {
                        status = "❌ Lock Failed"
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    CoroutineScope(Dispatchers.Main).launch {
                        status = "🔒 Lock Sent"
                    }
                }
            })
        }) {
            Text("🔒 Lock")
        }

        Spacer(modifier = Modifier.height(10.dp))

        // ⛔ SHUTDOWN
        Button(onClick = {
            val json = """{"command":"shutdown"}"""
            val body = json.toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url("$SERVER/command")
                .post(body)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    CoroutineScope(Dispatchers.Main).launch {
                        status = "❌ Failed"
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    CoroutineScope(Dispatchers.Main).launch {
                        status = "⛔ Shutdown Sent"
                    }
                }
            })
        }) {
            Text("⛔ Shutdown")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("Status: $status")
    }
}