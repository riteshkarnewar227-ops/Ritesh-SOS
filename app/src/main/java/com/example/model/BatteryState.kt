package com.example.model

data class BatteryState(
    val level: Int = 100,
    val isCharging: Boolean = false,
    val chargingSource: String = "Unplugged", // "AC", "USB", "Wireless", "Unplugged"
    val temperatureCelsius: Float = 28.0f,
    val voltageMilliVolts: Int = 4000,
    val health: String = "Good", // "Good", "Overheat", "Dead", "Over Voltage", "Unspecified Failure", "Cold"
    val isPowerSaveMode: Boolean = false,
    val technology: String = "Li-ion",
    val estimatedHoursRemaining: Float = 14.5f
) {
    val isCritical: Boolean get() = level <= 15
    val isLow: Boolean get() = level <= 30
    
    fun getTelemetrySummary(): String {
        val chargeText = if (isCharging) "Charging ($chargingSource)" else "Discharging"
        return "$level% ($chargeText, ${temperatureCelsius.toInt()}°C)"
    }
}
