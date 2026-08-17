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
import com.example.location.UserLocation
import com.example.model.SafeZone
import com.example.model.SosHistory
import com.example.model.UserProfile
import com.example.ui.theme.*

@Composable
fun PoliceHomeScreen(
    userProfile: UserProfile?,
    currentLocation: UserLocation,
    sosHistory: List<SosHistory>,
    safeZones: List<SafeZone>,
    onDispatchPcrVan: (incidentInfo: String, phone: String) -> Unit,
    onCallCitizen: (String) -> Unit,
    onOpenGoogleMaps: (Double, Double, String) -> Unit,
    onPlayAudioRecording: (String) -> Unit,
    onDialHelpline: (String) -> Unit,
    onOpenSwitchMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Life360DarkBg)
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Police Command Header
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
                        imageVector = Icons.Default.LocalPolice,
                        contentDescription = null,
                        tint = Life360Amber,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "POLICE EMERGENCY DISPATCH",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        color = Life360Amber.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "COMMAND DESK",
                            color = Life360Amber,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = "• ${userProfile?.policeStationOrBadge?.ifBlank { "Nagpur Police Control Room" }}",
                        color = Life360TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Switch Mode Button
            IconButton(
                onClick = onOpenSwitchMode,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Life360DarkSurface)
                    .border(1.dp, Life360DarkBorder, CircleShape)
                    .testTag("switch_mode_button")
            ) {
                Icon(
                    imageVector = Icons.Default.ManageAccounts,
                    contentDescription = "Switch Mode",
                    tint = Life360PurpleLight,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Live Radar Status Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .border(1.dp, Life360DarkBorder, RoundedCornerShape(18.dp)),
            colors = CardDefaults.cardColors(containerColor = Life360DarkSurface)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Life360Green.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Sensors,
                        contentDescription = null,
                        tint = Life360Green,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "EMERGENCY 112 DISPATCH ACTIVE",
                        color = Life360Green,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Monitoring live citizen SOS broadcasts & GPS distress coordinates in real time.",
                        color = Life360TextSecondary,
                        fontSize = 10.sp
                    )
                }
            }
        }

        // Active Emergency Incidents Section
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LIVE CITIZEN SOS INCIDENT QUEUE:",
                    color = Life360TextMuted,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                Surface(
                    color = Life360Red.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "${sosHistory.size.coerceAtLeast(1)} Alert Active",
                        color = Life360Red,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            // Primary Active Incident Card (either top from history or live demo incident)
            val topIncident = sosHistory.firstOrNull()
            val incidentAddress = topIncident?.address?.ifBlank { currentLocation.address } ?: "Near Sitabuldi Square, Nagpur"
            val incidentLat = topIncident?.latitude ?: currentLocation.latitude
            val incidentLng = topIncident?.longitude ?: currentLocation.longitude
            val incidentSender = topIncident?.senderName?.ifBlank { "Citizen / Child" } ?: "Citizen / Child"
            val incidentBattery = topIncident?.batteryPercent ?: 82

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(2.dp, Life360Red, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF240E14))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Header of Incident
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(Life360Red, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PriorityHigh,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "URGENT DISTRESS SIGNAL",
                                    color = Life360Red,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "$incidentSender • SOS Mobile Alert",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Surface(
                            color = Life360Red.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Life360Red)
                        ) {
                            Text(
                                text = "HIGH PRIORITY",
                                color = Life360Red,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Divider(color = Life360DarkBorder, thickness = 1.dp)

                    // Incident Location & Telemetry
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Life360Red,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = incidentAddress,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(start = 22.dp)
                        ) {
                            Text(
                                text = "GPS: ${String.format("%.4f, %.4f", incidentLat, incidentLng)}",
                                color = Life360TextSecondary,
                                fontSize = 10.sp
                            )
                            Text(
                                text = "Battery: $incidentBattery%",
                                color = Life360Amber,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Tactical Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 1. Dispatch PCR Van
                        Button(
                            onClick = {
                                val dispatchDetails = "🚨 PCR DISPATCH TO: $incidentAddress\nGPS: https://maps.google.com/?q=$incidentLat,$incidentLng\nSender: $incidentSender"
                                onDispatchPcrVan(dispatchDetails, "+917122561222")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Life360Red),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1.2f)
                                .height(40.dp)
                                .testTag("dispatch_pcr_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsCar,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Dispatch PCR",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // 2. Open Tactical Maps
                        OutlinedButton(
                            onClick = {
                                onOpenGoogleMaps(incidentLat, incidentLng, "SOS: $incidentSender")
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Life360PurpleLight),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Life360PurpleLight),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .testTag("police_tactical_map_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Map,
                                contentDescription = null,
                                tint = Life360PurpleLight,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Map",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // 3. Audio Recording Playback if present
                        if (topIncident?.hasAudioRecording == true && topIncident.audioFilePath.isNotBlank()) {
                            IconButton(
                                onClick = { onPlayAudioRecording(topIncident.audioFilePath) },
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Life360Green.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                    .border(1.dp, Life360Green, RoundedCornerShape(12.dp))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Listen Audio",
                                    tint = Life360Green,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Nagpur Police Station Grid
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "NAGPUR CITY POLICE STATIONS & PATROL POSTS:",
                color = Life360TextMuted,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            safeZones.filter { it.category == "Police Station" }.take(4).forEach { station ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, Life360DarkBorder, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Life360DarkSurface)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(Life360Amber.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalPolice,
                                contentDescription = null,
                                tint = Life360Amber,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = station.name,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = station.address,
                                color = Life360TextSecondary,
                                fontSize = 10.sp
                            )
                        }

                        IconButton(
                            onClick = { onDialHelpline(station.phoneNumber) },
                            modifier = Modifier
                                .size(36.dp)
                                .background(Life360DarkSurfaceElevated, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = "Call Station",
                                tint = Life360Green,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // Direct Helplines Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            PoliceHelplineButton(
                title = "112 Central",
                phone = "112",
                icon = Icons.Default.Emergency,
                tint = Life360Red,
                onClick = { onDialHelpline("112") },
                modifier = Modifier.weight(1f)
            )
            PoliceHelplineButton(
                title = "1091 Damini",
                phone = "1091",
                icon = Icons.Default.Security,
                tint = Life360Amber,
                onClick = { onDialHelpline("1091") },
                modifier = Modifier.weight(1f)
            )
            PoliceHelplineButton(
                title = "139 RPF Post",
                phone = "139",
                icon = Icons.Default.Train,
                tint = Life360PurpleLight,
                onClick = { onDialHelpline("139") },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun PoliceHelplineButton(
    title: String,
    phone: String,
    icon: ImageVector,
    tint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Life360DarkSurface,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Life360DarkBorder),
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = title,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

