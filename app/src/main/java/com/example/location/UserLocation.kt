package com.example.location

data class UserLocation(
    val latitude: Double = 21.1458, // Default Nagpur Central Coordinates
    val longitude: Double = 79.0882,
    val accuracy: Float = 5.0f,
    val altitude: Double = 310.0,
    val speed: Float = 0f,
    val bearing: Float = 0f,
    val timestamp: Long = System.currentTimeMillis(),
    val address: String = "Nagpur, Maharashtra, India",
    val isGpsLocked: Boolean = true,
    val isOfflineEstimate: Boolean = false
) {
    fun toGoogleMapsUrl(): String {
        return "https://maps.google.com/?q=$latitude,$longitude"
    }

    fun toFormattedCoordinates(): String {
        return "%.5f, %.5f".format(latitude, longitude)
    }
}
