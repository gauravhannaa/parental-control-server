package com.example.myapplication

import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import android.util.Log

object ApiHelper {

    private val client = OkHttpClient()

    // 🔥 NGROK URL
    private const val SERVER = "https://parental-control-server-jglv.onrender.com"

    // =========================
    // 📊 SEND LOGS (FIXED URL)
    // =========================
    fun sendLogs(app: String, time: String, duration: String) {

        val json = JSONObject()
        json.put("app", app)
        json.put("time", time)
        json.put("duration", duration)

        val body = json.toString()
            .toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("$SERVER/track") // ✅ FIXED HERE
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                Log.e("API_ERROR", "Fail: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("API_SUCCESS", "Logs Sent")
            }
        })
    }

    // =========================
    // 📍 SEND LOCATION (FIXED URL)
    // =========================
    fun sendLocation(lat: Double, lng: Double) {

        val json = JSONObject()
        json.put("lat", lat)
        json.put("lon", lng)

        val body = json.toString()
            .toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("$SERVER/location") // ✅ FIXED HERE
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                Log.e("LOCATION_ERROR", e.message.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("LOCATION", "Sent")
            }
        })
    }
}