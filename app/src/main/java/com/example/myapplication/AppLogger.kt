package com.example.myapplication

fun sendAppLog() {

    val appName = "WhatsApp"
    val time = System.currentTimeMillis().toString()
    val duration = "5 min"

    // ✅ direct helper use karo
    ApiHelper.sendLogs(appName, time, duration)
}