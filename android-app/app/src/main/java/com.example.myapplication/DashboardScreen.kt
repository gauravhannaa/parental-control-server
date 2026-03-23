package com.example.myapplication

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.database.*

@Composable
fun DashboardScreen() {

    var appName by remember { mutableStateOf("Loading...") }
    var time by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Connecting...") }

    val db = FirebaseDatabase.getInstance()

    // 🔥 listener ko safely handle karne ke liye
    DisposableEffect(Unit) {

        val ref = db.getReference("logs").limitToLast(1)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    for (child in snapshot.children) {
                        val data = child.value as? Map<*, *>

                        appName = data?.get("app")?.toString() ?: "No App"
                        time = data?.get("time")?.toString() ?: ""
                        duration = data?.get("duration")?.toString() ?: ""

                        status = "✅ Connected"
                    }
                } else {
                    status = "⚠ No Data"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                status = "❌ Firebase Error"
            }
        }

        ref.addValueEventListener(listener)

        // 🔥 IMPORTANT (memory leak fix)
        onDispose {
            ref.removeEventListener(listener)
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {

        Text("📊 Live Dashboard", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(10.dp))

        Text("📱 App: $appName")
        Text("🕒 Time: $time")
        Text("⏱ Duration: $duration")

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            db.getReference("command").setValue("lock")
            status = "🔒 Lock Sent"
        }) {
            Text("🔒 Lock")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = {
            db.getReference("command").setValue("shutdown")
            status = "⛔ Shutdown Sent"
        }) {
            Text("⛔ Shutdown")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = {
            db.getReference("command").setValue("alert")
            status = "📢 Alert Sent"
        }) {
            Text("📢 Alert")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("Status: $status")
    }
}