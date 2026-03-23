package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.*

@SuppressLint("MissingPermission")
fun startLocationUpdates(context: Context) {

    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        5000
    ).build()

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {

            val location = result.lastLocation ?: return

            val lat = location.latitude
            val lon = location.longitude

            // ✅ DIRECT API CALL
            ApiHelper.sendLocation(lat, lon)
        }
    }

    fusedLocationClient.requestLocationUpdates(
        locationRequest,
        locationCallback,
        null
    )
}