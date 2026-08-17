package com.example.battery

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.PowerManager
import com.example.model.BatteryState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BatteryMonitor(private val context: Context) {

    private val _batteryState = MutableStateFlow(fetchCurrentBatteryState())
    val batteryState: StateFlow<BatteryState> = _batteryState.asStateFlow()

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(c: Context?, intent: Intent?) {
            _batteryState.value = fetchCurrentBatteryState()
        }
    }

    private var isRegistered = false

    fun startListening() {
        if (!isRegistered) {
            val filter = IntentFilter().apply {
                addAction(Intent.ACTION_BATTERY_CHANGED)
                addAction(Intent.ACTION_POWER_CONNECTED)
                addAction(Intent.ACTION_POWER_DISCONNECTED)
                addAction(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)
            }
            context.registerReceiver(batteryReceiver, filter)
            isRegistered = true
            _batteryState.value = fetchCurrentBatteryState()
        }
    }

    fun stopListening() {
        if (isRegistered) {
            try {
                context.unregisterReceiver(batteryReceiver)
            } catch (_: Exception) {}
            isRegistered = false
        }
    }

    fun fetchCurrentBatteryState(): BatteryState {
        val iFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus: Intent? = context.registerReceiver(null, iFilter)

        val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        val isPowerSave = powerManager?.isPowerSaveMode ?: false

        if (batteryStatus == null) {
            return BatteryState(
                level = 85,
                isCharging = false,
                chargingSource = "Unplugged",
                temperatureCelsius = 29.5f,
                voltageMilliVolts = 3950,
                health = "Good",
                isPowerSaveMode = isPowerSave,
                technology = "Li-ion",
                estimatedHoursRemaining = 12.0f
            )
        }

        val level: Int = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale: Int = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val batteryPct: Int = if (level >= 0 && scale > 0) {
            ((level.toFloat() / scale.toFloat()) * 100).toInt()
        } else {
            85
        }

        val status: Int = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val isCharging: Boolean = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL

        val chargePlug: Int = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
        val chargingSource = when (chargePlug) {
            BatteryManager.BATTERY_PLUGGED_USB -> "USB Cable"
            BatteryManager.BATTERY_PLUGGED_AC -> "AC Fast Wall Charger"
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Wireless Dock"
            else -> if (isCharging) "Charging" else "Unplugged"
        }

        val tempRaw: Int = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)
        val tempCelsius = if (tempRaw > 0) tempRaw / 10.0f else 28.5f

        val voltageMv: Int = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 3900)

        val healthRaw: Int = batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_GOOD)
        val health = when (healthRaw) {
            BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat Alert"
            BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
            BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
            else -> "Normal"
        }

        val technology = batteryStatus.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "Li-ion"

        // Estimated hours remaining based on discharge rate
        val estHours = if (isCharging) {
            (100 - batteryPct) * 0.02f // Approx time to full
        } else {
            (batteryPct * 0.18f) // Approx hours of active safety runtime
        }

        return BatteryState(
            level = batteryPct,
            isCharging = isCharging,
            chargingSource = chargingSource,
            temperatureCelsius = tempCelsius,
            voltageMilliVolts = voltageMv,
            health = health,
            isPowerSaveMode = isPowerSave,
            technology = technology,
            estimatedHoursRemaining = estHours
        )
    }
}
