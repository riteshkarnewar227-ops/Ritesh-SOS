package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.location.UserLocation
import com.example.ui.components.BatteryCard
import com.example.ui.components.LocationStatusPill
import com.example.ui.components.SosPulsingButton
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.SafeGreen
import com.example.ui.theme.SafetyRed
import com.example.ui.theme.WarningAmber
import com.example.viewmodel.SosTriggerState

@Composable
fun SosHomeScreen(
    batteryState: BatteryState,
    userLocation: UserLocation,
    sosState: SosTriggerState,
    onTriggerSos: (String) -> Unit,
    onCancelCountdown: () -> Unit,
    onOpenBatteryScreen: () -> Unit,
    onOpenMapScreen: () -> Unit,
    onOpenGoogleMapsUrl: () -> Unit,
    onDialPolice: (String) -> Unit,
    onToggleAlarm: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCrisisTag by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0B132B))
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = SafetyRed,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "NAGPUR SURAKSHA",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }
                Text(
                    text = "Immediate Parent & Police SOS Dispatch",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )
            }

            // Quick Siren Button
            IconButton(
                onClick = onToggleAlarm,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (sosState.isAlarmActive) SafetyRed else Color(0xFF1E293B))
                    .border(1.dp, if (sosState.isAlarmActive) SafetyRed else DarkSurfaceBorder, CircleShape)
                    .testTag("siren_toggle_button")
            ) {
                Icon(
                    imageVector = if (sosState.isAlarmActive) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                    contentDescription = "Siren Alert",
                    tint = if (sosState.isAlarmActive) Color.White else Color(0xFF94A3B8)
                )
            }
        }

        // Live Location Bar
        LocationStatusPill(
            userLocation = userLocation,
            onOpenMap = onOpenMapScreen,
            onOpenGoogleMapsUrl = onOpenGoogleMapsUrl
        )

        // Hero Pulsing SOS Button
        SosPulsingButton(
            isCountingDown = sosState.isCountingDown,
            countdownSeconds = sosState.countdownSecondsRemaining,
            isSosActive = sosState.isSosActive,
            onTriggerSos = { onTriggerSos(selectedCrisisTag) },
            onCancelCountdown = onCancelCountdown
        )

        // Quick Crisis Situation Chips
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "OPTIONAL SITUATION TAG:",
                color = Color(0xFF64748B),
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CrisisTagChip(
                    label = "Unsafe Area",
                    isSelected = selectedCrisisTag == "Feeling unsafe in current area / suspicious activity",
                    onClick = {
                        selectedCrisisTag = if (selectedCrisisTag.startsWith("Feeling")) "" else "Feeling unsafe in current area / suspicious activity"
                    },
                    modifier = Modifier.weight(1f)
                )
                CrisisTagChip(
                    label = "Medical / Injury",
                    isSelected = selectedCrisisTag == "Medical Emergency / Severe injury or accident",
                    onClick = {
                        selectedCrisisTag = if (selectedCrisisTag.startsWith("Medical")) "" else "Medical Emergency / Severe injury or accident"
                    },
                    modifier = Modifier.weight(1f)
                )
                CrisisTagChip(
                    label = "Following / Stalker",
                    isSelected = selectedCrisisTag == "Urgent: Being followed or harassed",
                    onClick = {
                        selectedCrisisTag = if (selectedCrisisTag.startsWith("Urgent")) "" else "Urgent: Being followed or harassed"
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Live Battery Monitor Card
        BatteryCard(
            batteryState = batteryState,
            onOpenBatteryDetails = onOpenBatteryScreen
        )

        // Quick Emergency Helplines Grid
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "DIRECT CRISIS SPEED DIAL:",
                color = Color(0xFF64748B),
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SpeedDialCard(
                    title = "Police 112",
                    subtitle = "Nagpur Command",
                    phone = "112",
                    icon = Icons.Default.LocalPolice,
                    tint = SafetyRed,
                    onClick = { onDialPolice("112") },
                    modifier = Modifier.weight(1f)
                )
                SpeedDialCard(
                    title = "Women Helpline",
                    subtitle = "Damini Cell 1091",
                    phone = "1091",
                    icon = Icons.Default.Security,
                    tint = WarningAmber,
                    onClick = { onDialPolice("1091") },
                    modifier = Modifier.weight(1f)
                )
                SpeedDialCard(
                    title = "Ambulance",
                    subtitle = "Emergency 108",
                    phone = "108",
                    icon = Icons.Default.MedicalServices,
                    tint = SafeGreen,
                    onClick = { onDialPolice("108") },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun CrisisTagChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) SafetyRed.copy(alpha = 0.25f) else Color(0xFF1E293B),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) SafetyRed else DarkSurfaceBorder
        ),
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
    ) {
        Text(
            text = label,
            color = if (isSelected) Color(0xFFFFD8D8) else Color(0xFFCBD5E1),
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
            maxLines = 1
        )
    }
}

@Composable
private fun SpeedDialCard(
    title: String,
    subtitle: String,
    phone: String,
    icon: ImageVector,
    tint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(12.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(tint.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
            Text(
                text = subtitle,
                color = Color(0xFF94A3B8),
                fontSize = 9.sp
            )
        }
    }
}
