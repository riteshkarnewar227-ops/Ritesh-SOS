package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.location.UserLocation
import com.example.model.BatteryState
import com.example.model.EmergencyContact
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context matches app name`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Nagpur Suraksha", appName)
    }

    @Test
    fun `battery state telemetry formatting`() {
        val battery = BatteryState(
            level = 88,
            isCharging = true,
            chargingSource = "AC Fast Wall Charger",
            temperatureCelsius = 31.0f
        )
        val summary = battery.getTelemetrySummary()
        assertTrue(summary.contains("88%"))
        assertTrue(summary.contains("Charging"))
    }

    @Test
    fun `user location google maps url generation`() {
        val loc = UserLocation(latitude = 21.1458, longitude = 79.0882)
        val url = loc.toGoogleMapsUrl()
        assertEquals("https://maps.google.com/?q=21.1458,79.0882", url)
    }
}
