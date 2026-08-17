package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emergency.AudioRecordingState
import com.example.location.UserLocation
import com.example.model.BatteryState
import com.example.model.UserProfile
import com.example.ui.components.SosPulsingButton
import com.example.ui.theme.*
import com.example.viewmodel.SosTriggerState

@Composable
fun ChildHomeScreen(
    userProfile: UserProfile?,
    batteryState: BatteryState,
    userLocation: UserLocation,
    sosState: SosTriggerState,
    audioState: AudioRecordingState,
    onTriggerSos: (String) -> Unit,
    onCancelCountdown: () -> Unit,
    onOpenBatteryScreen: () -> Unit,
    onOpenMapScreen: () -> Unit,
    onOpenSafeRouteScreen: () -> Unit,
    onOpenGoogleMapsUrl: () -> Unit,
    onDialPolice: (String) -> Unit,
    onToggleAlarm: () -> Unit,
    onStartAudioRecording: () -> Unit,
    onStopAudioRecording: () -> Unit,
    onPlayAudioRecording: () -> Unit,
    onStopAudioPlayback: () -> Unit,
    onShareAudioWhatsApp: () -> Unit,
    onSharePairingCodeWhatsApp: (String) -> Unit,
    onOpenSwitchMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCrisisTag by remember { mutableStateOf("") }
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
        // 1. Sleek Minimalist App Bar / Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Life360Purple)
                        .testTag("user_avatar_bubble"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (userProfile?.name?.take(1) ?: "U").uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                Column {
                    Text(
                        text = userProfile?.name ?: "Priya Sharma",
                        color = Life360TextPrimary,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(Life360Green, CircleShape)
                        )
                        Text(
                            text = "Circle Protected",
                            color = Life360Green,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Quick Status Actions: Siren + Battery pill + Mode
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Battery Pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (batteryState.isLow) Life360RedBg else Life360LightSurface,
                    border = BorderStroke(1.dp, if (batteryState.isLow) Life360Red else Life360LightBorder),
                    modifier = Modifier.clickable { onOpenBatteryScreen() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = if (batteryState.isCharging) Icons.Default.Bolt else Icons.Default.BatteryStd,
                            contentDescription = "Battery",
                            tint = if (batteryState.isLow) Life360Red else Life360Green,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "${batteryState.level}%",
                            color = Life360TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Loud Alarm Siren Icon Button
                IconButton(
                    onClick = onToggleAlarm,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (sosState.isAlarmActive) Life360Red else Life360LightSurface)
                        .border(1.dp, if (sosState.isAlarmActive) Life360Red else Life360LightBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = if (sosState.isAlarmActive) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                        contentDescription = "Siren",
                        tint = if (sosState.isAlarmActive) Color.White else Life360TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Switch Role / Mode
                IconButton(
                    onClick = onOpenSwitchMode,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Life360PurpleBg)
                        .border(1.dp, Life360Purple.copy(alpha = 0.25f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Switch Mode",
                        tint = Life360Purple,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // 2. HERO SOS BUTTON (Prominent, unmissable, clean)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, Life360LightBorder, RoundedCornerShape(24.dp))
                .shadow(4.dp, RoundedCornerShape(24.dp), ambientColor = Life360PurpleBg, spotColor = Life360Purple.copy(alpha = 0.1f)),
            colors = CardDefaults.cardColors(containerColor = Life360LightSurface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Subtitle helper
                Text(
                    text = if (sosState.isCountingDown) "DISPATCHING IN ${sosState.countdownSecondsRemaining}s" else if (sosState.isSosActive) "EMERGENCY DISPATCH ACTIVE" else "EMERGENCY SOS",
                    color = if (sosState.isCountingDown || sosState.isSosActive) Life360Red else Life360PurpleDark,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )

                // Pulsing SOS Trigger
                SosPulsingButton(
                    isCountingDown = sosState.isCountingDown,
                    countdownSeconds = sosState.countdownSecondsRemaining,
                    isSosActive = sosState.isSosActive,
                    onTriggerSos = { onTriggerSos(selectedCrisisTag) },
                    onCancelCountdown = onCancelCountdown
                )

                Text(
                    text = if (sosState.isCountingDown) "Tap button to Cancel countdown" else "Tap for 3-second auto alert to Family & Police",
                    color = Life360TextSecondary,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center
                )

                // Quick Situational Tag Selector (Clean 1-Tap Pills)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val situations = listOf(
                        "🚶 Unsafe Area" to "Feeling unsafe in current area / suspicious activity",
                        "🚑 Medical" to "Medical Emergency / Severe injury or accident",
                        "⚠️ Harassed" to "Urgent: Being followed or harassed"
                    )

                    situations.forEach { (label, reason) ->
                        val isSelected = selectedCrisisTag == reason
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) Life360Purple else Life360LightSurfaceElevated,
                            border = BorderStroke(1.dp, if (isSelected) Life360Purple else Life360LightBorder),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    selectedCrisisTag = if (isSelected) "" else reason
                                }
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) Color.White else Life360TextPrimary,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                modifier = Modifier.padding(vertical = 7.dp, horizontal = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // 3. 4 HIGH-VISIBILITY CORE ACTION TILES (Clean 2x2 Bento Grid)
        Text(
            text = "QUICK SAFETY HUB",
            color = Life360TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Tile 1: Safe Route Advisor
            QuickActionTile(
                icon = Icons.Default.AltRoute,
                iconColor = Life360Purple,
                iconBg = Life360PurpleBg,
                title = "Safe Routes",
                subtitle = "Score & Hazards",
                badge = "92% Safe",
                badgeColor = Life360Green,
                onClick = onOpenSafeRouteScreen,
                modifier = Modifier.weight(1f),
                testTag = "tile_safe_route"
            )

            // Tile 2: Live Circle Map
            QuickActionTile(
                icon = Icons.Default.LocationOn,
                iconColor = Life360Indigo,
                iconBg = Life360Indigo.copy(alpha = 0.12f),
                title = "Live GPS Map",
                subtitle = userLocation.address.take(18) + "...",
                badge = "Live GPS",
                badgeColor = Life360Indigo,
                onClick = onOpenMapScreen,
                modifier = Modifier.weight(1f),
                testTag = "tile_live_map"
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Tile 3: Distress Audio Proof
            val isRecording = audioState.isRecording
            val hasRecorded = !audioState.lastRecordedFilePath.isNullOrBlank()
            QuickActionTile(
                icon = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                iconColor = if (isRecording) Life360Red else Life360Amber,
                iconBg = if (isRecording) Life360RedBg else Life360AmberBg,
                title = if (isRecording) "Recording..." else "Distress Audio",
                subtitle = if (hasRecorded) "Play / Share 30s" else "Tap to record proof",
                badge = if (isRecording) "LIVE REC" else if (hasRecorded) "SAVED" else "30s Mic",
                badgeColor = if (isRecording) Life360Red else Life360Amber,
                onClick = {
                    if (isRecording) {
                        onStopAudioRecording()
                    } else if (hasRecorded) {
                        if (audioState.isPlaying) onStopAudioPlayback() else onPlayAudioRecording()
                    } else {
                        onStartAudioRecording()
                    }
                },
                modifier = Modifier.weight(1f),
                testTag = "tile_audio_distress"
            )

            // Tile 4: Emergency Police 112
            QuickActionTile(
                icon = Icons.Default.LocalPolice,
                iconColor = Life360Red,
                iconBg = Life360RedBg,
                title = "Police 112",
                subtitle = "Instant Emergency",
                badge = "Direct Call",
                badgeColor = Life360Red,
                onClick = { onDialPolice("112") },
                modifier = Modifier.weight(1f),
                testTag = "tile_police_112"
            )
        }

        // 4. Sleek Family Circle Bar (Invite Code & Quick WhatsApp Share)
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Life360LightSurface,
            border = BorderStroke(1.dp, Life360LightBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(Life360PurpleBg, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.People,
                            contentDescription = null,
                            tint = Life360Purple,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "FAMILY CIRCLE CODE",
                            color = Life360TextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = userProfile?.pairingCode ?: "SUR-8921",
                            color = Life360PurpleDark,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Button(
                    onClick = { onSharePairingCodeWhatsApp(userProfile?.pairingCode ?: "SUR-8921") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Life360Green,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Share Code",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun QuickActionTile(
    icon: ImageVector,
    iconColor: Color,
    iconBg: Color,
    title: String,
    subtitle: String,
    badge: String,
    badgeColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, Life360LightBorder, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .testTag(testTag),
        colors = CardDefaults.cardColors(containerColor = Life360LightSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(iconBg, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = badgeColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = badge,
                        color = badgeColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                Text(
                    text = title,
                    color = Life360TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    color = Life360TextSecondary,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
