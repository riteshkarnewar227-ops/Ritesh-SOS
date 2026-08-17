package com.example.location

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Looper
import com.example.model.SafeZone
import com.google.android.gms.location.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class LocationTracker(
    private val context: Context,
    private val scope: CoroutineScope
) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val prefs: SharedPreferences =
        context.getSharedPreferences("location_cache_prefs", Context.MODE_PRIVATE)

    private val _currentLocation = MutableStateFlow(loadCachedLocation())
    val currentLocation: StateFlow<UserLocation> = _currentLocation.asStateFlow()

    private var isTracking = false

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.lastLocation?.let { loc ->
                updateFromLocation(loc, isOffline = false)
            }
        }
    }

    private fun loadCachedLocation(): UserLocation {
        val lat = prefs.getFloat("cached_lat", 21.1458f).toDouble()
        val lng = prefs.getFloat("cached_lng", 79.0882f).toDouble()
        val acc = prefs.getFloat("cached_acc", 10.0f)
        val addr = prefs.getString("cached_addr", "Nagpur, Maharashtra") ?: "Nagpur, Maharashtra"
        val time = prefs.getLong("cached_time", System.currentTimeMillis())

        return UserLocation(
            latitude = lat,
            longitude = lng,
            accuracy = acc,
            address = addr,
            timestamp = time,
            isGpsLocked = true,
            isOfflineEstimate = true
        )
    }

    private fun saveCachedLocation(loc: UserLocation) {
        prefs.edit()
            .putFloat("cached_lat", loc.latitude.toFloat())
            .putFloat("cached_lng", loc.longitude.toFloat())
            .putFloat("cached_acc", loc.accuracy)
            .putString("cached_addr", loc.address)
            .putLong("cached_time", loc.timestamp)
            .apply()
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        if (isTracking) return

        try {
            // First check last location
            fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                if (loc != null) {
                    updateFromLocation(loc, isOffline = false)
                }
            }

            val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L)
                .setMinUpdateIntervalMillis(2000L)
                .setMinUpdateDistanceMeters(1.0f)
                .build()

            fusedLocationClient.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper()
            )
            isTracking = true
        } catch (_: SecurityException) {
            // Permission not yet granted, fallback to cached
        }
    }

    fun stopLocationUpdates() {
        if (isTracking) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
            isTracking = false
        }
    }

    private fun updateFromLocation(loc: Location, isOffline: Boolean) {
        scope.launch(Dispatchers.IO) {
            val addressString = resolveAddress(loc.latitude, loc.longitude)
            val updated = UserLocation(
                latitude = loc.latitude,
                longitude = loc.longitude,
                accuracy = loc.accuracy,
                altitude = loc.altitude,
                speed = loc.speed,
                bearing = loc.bearing,
                timestamp = System.currentTimeMillis(),
                address = addressString,
                isGpsLocked = true,
                isOfflineEstimate = isOffline
            )
            _currentLocation.value = updated
            saveCachedLocation(updated)
        }
    }

    private fun resolveAddress(lat: Double, lng: Double): String {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val addresses = geocoder.getFromLocation(lat, lng, 1)
                if (!addresses.isNullOrEmpty()) {
                    val addr = addresses[0]
                    val feature = addr.featureName ?: ""
                    val locality = addr.locality ?: addr.subAdminArea ?: "Nagpur"
                    val line = addr.getAddressLine(0) ?: "$feature, $locality"
                    line
                } else {
                    "Nagpur, Maharashtra (%.4f, %.4f)".format(lat, lng)
                }
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(lat, lng, 1)
                if (!addresses.isNullOrEmpty()) {
                    val addr = addresses[0]
                    addr.getAddressLine(0) ?: "${addr.locality ?: "Nagpur"}, Maharashtra"
                } else {
                    "Nagpur, Maharashtra (%.4f, %.4f)".format(lat, lng)
                }
            }
        } catch (_: Exception) {
            "Nagpur City Area (Lat: %.4f, Lng: %.4f)".format(lat, lng)
        }
    }

    fun calculateDistanceMeters(targetLat: Double, targetLng: Double): Float {
        val curr = _currentLocation.value
        val results = FloatArray(1)
        Location.distanceBetween(curr.latitude, curr.longitude, targetLat, targetLng, results)
        return results[0]
    }

    fun findNearestSafeZone(zones: List<SafeZone>): Pair<SafeZone, Float>? {
        if (zones.isEmpty()) return null
        val curr = _currentLocation.value
        var nearest: SafeZone? = null
        var minDistance = Float.MAX_VALUE

        for (z in zones) {
            val results = FloatArray(1)
            Location.distanceBetween(curr.latitude, curr.longitude, z.latitude, z.longitude, results)
            if (results[0] < minDistance) {
                minDistance = results[0]
                nearest = z
            }
        }

        return nearest?.let { it to minDistance }
    }
}
