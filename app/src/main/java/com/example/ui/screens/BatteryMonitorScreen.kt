package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BatteryState
import com.example.ui.theme.*

@Composable
fun BatteryMonitorScreen(
    batteryState: BatteryState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val batteryColor = when {
        batteryState.level > 50 -> Life360Green
        batteryState.level > 20 -> Life360Amber
        else -> Life360Red
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Life360DarkBg)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top App Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Life360DarkSurfaceElevated)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Column {
                Text(
                    text = "DEVICE BATTERY HEALTH",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Real-time Telemetry & SOS Power Attachment",
                    color = Life360TextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        // Hero Battery Status Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, Life360DarkBorder, RoundedCornerShape(20.dp))
                .testTag("battery_hero_card"),
            colors = CardDefaults.cardColors(containerColor = Life360DarkSurface)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Large Battery Readout
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .background(batteryColor.copy(alpha = 0.15f), CircleShape)
                        .border(3.dp, batteryColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${batteryState.level}%",
                            color = batteryColor,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = if (batteryState.isCharging) "CHARGING" else "BATTERY",
                            color = Life360TextSecondary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Text(
                    text = if (batteryState.isCharging) {
                        "Connected to ${batteryState.chargingSource}"
                    } else {
                        "Estimated ${"%.1f".format(batteryState.estimatedHoursRemaining)} hours safety runtime remaining"
                    },
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )

                // Critical Low Warning if applicable
                if (batteryState.isCritical) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Life360Red.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Life360Red)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Life360Red,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "CRITICAL BATTERY: Please connect to a charger or trigger emergency SOS before device powers down.",
                                color = Color(0xFFFFD8D8),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // Detailed Telemetry Grid
        Text(
            text = "HARDWARE TELEMETRY SPECIFICATIONS",
            color = Life360TextSecondary,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TelemetryDetailCard(
                title = "Temperature",
                value = "${batteryState.temperatureCelsius}°C",
                status = if (batteryState.temperatureCelsius > 42) "Warm" else "Normal",
                icon = Icons.Default.Thermostat,
                tint = if (batteryState.temperatureCelsius > 42) Life360Amber else Life360Green,
                modifier = Modifier.weight(1f)
            )

            TelemetryDetailCard(
                title = "Voltage",
                value = "${batteryState.voltageMilliVolts} mV",
                status = "Nominal",
                icon = Icons.Default.ElectricBolt,
                tint = Life360PurpleLight,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TelemetryDetailCard(
                title = "Health State",
                value = batteryState.health,
                status = "Good",
                icon = Icons.Default.Favorite,
                tint = Life360Green,
                modifier = Modifier.weight(1f)
            )

            TelemetryDetailCard(
                title = "Power Saver",
                value = if (batteryState.isPowerSaveMode) "Active" else "Disabled",
                status = if (batteryState.isPowerSaveMode) "Conserving" else "Standard",
                icon = Icons.Default.EnergySavingsLeaf,
                tint = if (batteryState.isPowerSaveMode) Life360Amber else Life360TextMuted,
                modifier = Modifier.weight(1f)
            )
        }

        // WhatsApp SOS Payload Integration preview
        Text(
            text = "EMERGENCY SOS PAYLOAD ATTACHMENT",
            color = Life360TextSecondary,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, Life360DarkBorder, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = Life360DarkSurface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = null,
                        tint = Life360Green,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "WhatsApp Alert Telemetry Line:",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Life360DarkSurfaceElevated,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "🔋 Device Battery: ${batteryState.getTelemetrySummary()}",
                        color = Life360Green,
                        fontSize = 12.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Text(
                    text = "Police control room and Parents will know if your device is running out of power so they can expedite emergency dispatch.",
                    color = Life360TextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun TelemetryDetailCard(
    title: String,
    value: String,
    status: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, Life360DarkBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Life360DarkSurface)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = Life360TextSecondary,
                    fontSize = 11.sp
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(16.dp)
                )
            }

            Text(
                text = value,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = status,
                color = tint,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

