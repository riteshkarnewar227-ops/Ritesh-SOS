package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BatteryState
import com.example.ui.theme.*

@Composable
fun BatteryCard(
    batteryState: BatteryState,
    onOpenBatteryDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    val batteryFraction by animateFloatAsState(
        targetValue = (batteryState.level.coerceIn(0, 100)) / 100f,
        label = "battery_fill"
    )

    val batteryColor by animateColorAsState(
        targetValue = when {
            batteryState.level > 50 -> Life360Green
            batteryState.level > 20 -> Life360Amber
            else -> Life360Red
        },
        label = "battery_color"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, Life360DarkBorder, RoundedCornerShape(20.dp))
            .clickable { onOpenBatteryDetails() }
            .testTag("battery_monitor_card"),
        colors = CardDefaults.cardColors(
            containerColor = Life360DarkSurface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(batteryColor.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (batteryState.isCharging) Icons.Default.Bolt else Icons.Default.BatteryStd,
                            contentDescription = "Battery Status",
                            tint = batteryColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "CIRCLE DEVICE BATTERY",
                            style = MaterialTheme.typography.labelSmall,
                            color = Life360TextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = if (batteryState.isCharging) "Charging (${batteryState.chargingSource})" else "On Battery Mode",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Battery Level Percentage Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = batteryColor.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, batteryColor.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "${batteryState.level}%",
                        color = batteryColor,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Life360DarkBg)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(batteryFraction)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(batteryColor.copy(alpha = 0.7f), batteryColor)
                            )
                        )
                )
            }

            // Quick Telemetry Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TelemetryBadge(
                    icon = Icons.Default.Thermostat,
                    label = "${batteryState.temperatureCelsius.toInt()}°C",
                    description = "Temp"
                )
                TelemetryBadge(
                    icon = Icons.Default.ElectricBolt,
                    label = "${batteryState.voltageMilliVolts}mV",
                    description = "Voltage"
                )
                TelemetryBadge(
                    icon = Icons.Default.Favorite,
                    label = batteryState.health,
                    description = "Health"
                )
                TelemetryBadge(
                    icon = Icons.Default.Timer,
                    label = "~${"%.1f".format(batteryState.estimatedHoursRemaining)}h",
                    description = "Runtime"
                )
            }

            // Auto-Included in WhatsApp alert banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Life360Green.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Life360Green,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "Live battery level automatically sent with SOS emergency payloads.",
                    color = Life360Green,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun TelemetryBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    description: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Life360TextMuted,
            modifier = Modifier.size(12.dp)
        )
        Column {
            Text(
                text = label,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                color = Life360TextSecondary,
                fontSize = 9.sp
            )
        }
    }
}

