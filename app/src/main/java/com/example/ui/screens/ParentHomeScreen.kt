package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.location.UserLocation
import com.example.model.BatteryState
import com.example.model.SafeZone
import com.example.model.SosHistory
import com.example.model.UserProfile
import com.example.ui.components.Life360Header
import com.example.ui.components.Life360MapCard
import com.example.ui.theme.*

@Composable
fun ParentHomeScreen(
    userProfile: UserProfile?,
    childLocation: UserLocation,
    childBattery: BatteryState,
    sosHistory: List<SosHistory>,
    safeZones: List<SafeZone>,
    onUpdatePairingCode: (code: String, childName: String, childPhone: String) -> Unit,
    onCallChild: (String) -> Unit,
    onWhatsAppCheckIn: (childPhone: String, childName: String) -> Unit,
    onOpenGoogleMaps: (Double, Double, String) -> Unit,
    onForwardToPolice: (String) -> Unit,
    onPlayAudioRecording: (String) -> Unit,
    onOpenSwitchMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showPairingDialog by remember { mutableStateOf(false) }
    var inputCode by remember { mutableStateOf(userProfile?.pairedWithCode ?: "") }
    var inputChildName by remember { mutableStateOf(userProfile?.pairedPersonName ?: "") }
    var inputChildPhone by remember { mutableStateOf(userProfile?.pairedPersonPhone ?: "") }

    var inputPasskey by remember { mutableStateOf("") }
    var pairingError by remember { mutableStateOf("") }

    val hasPairedChild = !userProfile?.pairedWithCode.isNullOrBlank() || !userProfile?.pairedPersonPhone.isNullOrBlank()
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Life360LightBg)
            .verticalScroll(scrollState)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Life360 Style Header
        Life360Header(
            userProfile = userProfile,
            batteryState = childBattery,
            isAlarmActive = false,
            onToggleAlarm = { },
            onOpenSwitchMode = onOpenSwitchMode
        )

        // Child Pairing Status / Quick Link Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, Life360LightBorder, RoundedCornerShape(20.dp))
                .testTag("paired_child_card"),
            colors = CardDefaults.cardColors(containerColor = Life360LightSurface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Life360Purple)
                                .border(2.dp, Life360Green, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (userProfile?.pairedPersonName?.take(1) ?: "C").uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }

                        Column {
                            Text(
                                text = if (hasPairedChild) (userProfile?.pairedPersonName ?: "Ward / Child") else "No Member Connected",
                                color = Life360TextPrimary,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (hasPairedChild) "Code: ${userProfile?.pairedWithCode ?: "SUR-0000"}" else "Tap 'Pair Member' to link device",
                                color = Life360TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Button(
                        onClick = { showPairingDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Life360PurpleBg,
                            contentColor = Life360PurpleDark
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("pair_child_button")
                    ) {
                        Icon(
                            imageVector = if (hasPairedChild) Icons.Default.Edit else Icons.Default.AddLink,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (hasPairedChild) "Edit" else "Pair Member",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (hasPairedChild) {
                    Divider(color = Life360LightBorder)

                    // Member Live Telemetry (Battery, Location, Speed)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (childBattery.isCharging) Icons.Default.Bolt else Icons.Default.BatteryStd,
                                contentDescription = null,
                                tint = if (childBattery.isLow) Life360Red else Life360Green,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "${childBattery.level}% Battery",
                                color = Life360TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = null,
                                tint = Life360Purple,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = childLocation.address.take(24) + "...",
                                color = Life360TextSecondary,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // 1-Tap Quick Action Bar (Call, WhatsApp Ping, Google Maps)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ActionPill(
                            label = "Call",
                            icon = Icons.Default.Call,
                            tint = Life360Green,
                            onClick = { onCallChild(userProfile?.pairedPersonPhone ?: "") },
                            modifier = Modifier.weight(1f)
                        )
                        ActionPill(
                            label = "WhatsApp",
                            icon = Icons.Default.Chat,
                            tint = Color(0xFF25D366),
                            onClick = { onWhatsAppCheckIn(userProfile?.pairedPersonPhone ?: "", userProfile?.pairedPersonName ?: "Child") },
                            modifier = Modifier.weight(1f)
                        )
                        ActionPill(
                            label = "Navigate",
                            icon = Icons.Default.Navigation,
                            tint = Life360Purple,
                            onClick = { onOpenGoogleMaps(childLocation.latitude, childLocation.longitude, userProfile?.pairedPersonName ?: "Child Location") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Life360 Interactive Live Map Card
        Life360MapCard(
            userLocation = childLocation,
            safeZones = safeZones,
            onOpenFullMap = { onOpenGoogleMaps(childLocation.latitude, childLocation.longitude, "Live Map") },
            onOpenGoogleMaps = { onOpenGoogleMaps(childLocation.latitude, childLocation.longitude, "Child GPS") }
        )

        // Safe Places & Geofence Zones (Life360 Style)
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
                    text = "FAMILY PLACES & SAFE ZONES",
                    color = Life360TextMuted,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "${safeZones.size} Safe Spots",
                    color = Life360PurpleDark,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            safeZones.take(3).forEach { zone ->
                PlaceItemCard(
                    zone = zone,
                    onNavigate = { onOpenGoogleMaps(zone.latitude, zone.longitude, zone.name) },
                    onCall = { onCallChild(zone.phoneNumber) }
                )
            }
        }

        // Recent SOS Emergency Alerts
        if (sosHistory.isNotEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "RECENT SAFETY ALERTS (${sosHistory.size})",
                    color = Life360Red,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                sosHistory.take(3).forEach { alert ->
                    AlertHistoryItemCard(
                        alert = alert,
                        onOpenMap = { onOpenGoogleMaps(alert.latitude, alert.longitude, "Alert: ${alert.senderName}") },
                        onPlayAudio = { onPlayAudioRecording(alert.audioFilePath) },
                        onForwardPolice = { onForwardToPolice("112") }
                    )
                }
            }
        }
    }

    // Pairing Dialog
    if (showPairingDialog) {
        AlertDialog(
            onDismissRequest = {
                showPairingDialog = false
                pairingError = ""
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp),
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Link,
                        contentDescription = null,
                        tint = Life360Green,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Connect Member Dashboard",
                        color = Life360TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Enter the Safety Code and Security Passkey shown on your member's screen to connect dashboards:",
                        color = Life360TextSecondary,
                        fontSize = 11.sp
                    )
                    OutlinedTextField(
                        value = inputCode,
                        onValueChange = { inputCode = it.uppercase(); pairingError = "" },
                        label = { Text("Member Safety Code (e.g. SUR-8492)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Life360TextPrimary,
                            unfocusedTextColor = Life360TextPrimary,
                            focusedBorderColor = Life360Green,
                            unfocusedBorderColor = Life360LightBorder
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = inputPasskey,
                        onValueChange = { inputPasskey = it; pairingError = "" },
                        label = { Text("Member's Security Passkey / PIN") },
                        placeholder = { Text("Default: 1234") },
                        singleLine = true,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Life360Green,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Life360TextPrimary,
                            unfocusedTextColor = Life360TextPrimary,
                            focusedBorderColor = Life360Green,
                            unfocusedBorderColor = Life360LightBorder
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = inputChildName,
                        onValueChange = { inputChildName = it },
                        label = { Text("Member's Name (e.g. Aarav)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Life360TextPrimary,
                            unfocusedTextColor = Life360TextPrimary,
                            focusedBorderColor = Life360Green,
                            unfocusedBorderColor = Life360LightBorder
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = inputChildPhone,
                        onValueChange = { inputChildPhone = it },
                        label = { Text("Member's Phone (+91...)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Life360TextPrimary,
                            unfocusedTextColor = Life360TextPrimary,
                            focusedBorderColor = Life360Green,
                            unfocusedBorderColor = Life360LightBorder
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (pairingError.isNotBlank()) {
                        Text(
                            text = pairingError,
                            color = Life360Red,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (inputCode.isBlank()) {
                            pairingError = "Please enter the member's Safety Code"
                            return@Button
                        }
                        if (inputPasskey.isBlank()) {
                            pairingError = "Please enter the member's Security Passkey"
                            return@Button
                        }
                        onUpdatePairingCode(inputCode, inputChildName.ifBlank { "Child / Family Member" }, inputChildPhone)
                        showPairingDialog = false
                        pairingError = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Life360Green),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Verify & Connect", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showPairingDialog = false
                        pairingError = ""
                    }
                ) {
                    Text("Cancel", color = Life360TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun ActionPill(
    label: String,
    icon: ImageVector,
    tint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Life360LightSurfaceElevated,
        border = BorderStroke(1.dp, Life360LightBorder),
        modifier = modifier
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                color = Life360TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun PlaceItemCard(
    zone: SafeZone,
    onNavigate: () -> Unit,
    onCall: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Life360LightSurface,
        border = BorderStroke(1.dp, Life360LightBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Life360PurpleBg, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (zone.category.contains("Police")) Icons.Default.LocalPolice else Icons.Default.LocalHospital,
                        contentDescription = null,
                        tint = Life360Purple,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column {
                    Text(
                        text = zone.name,
                        color = Life360TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${zone.category} • ${zone.address}",
                        color = Life360TextSecondary,
                        fontSize = 10.sp
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                IconButton(
                    onClick = onCall,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Life360GreenBg)
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = null,
                        tint = Life360Green,
                        modifier = Modifier.size(16.dp)
                    )
                }
                IconButton(
                    onClick = onNavigate,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Life360PurpleBg)
                ) {
                    Icon(
                        imageVector = Icons.Default.Directions,
                        contentDescription = null,
                        tint = Life360Purple,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AlertHistoryItemCard(
    alert: SosHistory,
    onOpenMap: () -> Unit,
    onPlayAudio: () -> Unit,
    onForwardPolice: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Life360RedBg,
        border = BorderStroke(1.dp, Life360Red.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Life360Red, CircleShape)
                    )
                    Text(
                        text = "EMERGENCY SOS: ${alert.senderName}",
                        color = Life360Red,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
                Text(
                    text = "${alert.batteryPercent}% ⚡",
                    color = Life360Amber,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "📍 ${alert.address}",
                color = Life360TextSecondary,
                fontSize = 11.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onOpenMap,
                    colors = ButtonDefaults.buttonColors(containerColor = Life360Purple),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Place, contentDescription = null, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("View GPS", fontSize = 10.sp, color = Color.White)
                }

                if (alert.hasAudioRecording && alert.audioFilePath.isNotBlank()) {
                    Button(
                        onClick = onPlayAudio,
                        colors = ButtonDefaults.buttonColors(containerColor = Life360PurpleBg),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Life360PurpleDark, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Listen Audio", fontSize = 10.sp, color = Life360PurpleDark)
                    }
                }
            }
        }
    }
}
