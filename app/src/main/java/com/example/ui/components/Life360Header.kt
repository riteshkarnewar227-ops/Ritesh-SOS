package com.example.ui.components

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BatteryState
import com.example.model.UserProfile
import com.example.model.UserRole
import com.example.ui.theme.*

@Composable
fun Life360Header(
    userProfile: UserProfile?,
    batteryState: BatteryState,
    isAlarmActive: Boolean,
    onToggleAlarm: () -> Unit,
    onOpenSwitchMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val role = userProfile?.role ?: UserRole.CHILD
    val userName = userProfile?.name?.ifBlank { "You" } ?: "You"
    val pairedName = userProfile?.pairedPersonName?.ifBlank { "Guardian" } ?: "Guardian"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Top Row: Circle Switcher & Quick Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Life360 Family Circle Dropdown Pill
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Life360DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, Life360DarkBorder),
                modifier = Modifier
                    .clickable { onOpenSwitchMode() }
                    .testTag("life360_circle_selector")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(Life360Green, CircleShape)
                    )
                    Text(
                        text = when (role) {
                            UserRole.CHILD -> "Family Circle"
                            UserRole.PARENT -> "Guardian Circle"
                            UserRole.POLICE -> "Nagpur Police Grid"
                        },
                        color = Life360TextPrimary,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Life360PurpleLight,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Top Right Action Buttons (Siren Alert + Switch Profile)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Siren Panic Sound
                IconButton(
                    onClick = onToggleAlarm,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (isAlarmActive) Life360Red else Life360DarkSurface)
                        .border(1.dp, if (isAlarmActive) Life360Red else Life360DarkBorder, CircleShape)
                        .testTag("siren_toggle_button")
                ) {
                    Icon(
                        imageVector = if (isAlarmActive) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                        contentDescription = "Siren Alert",
                        tint = if (isAlarmActive) Color.White else Life360TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Profile / Mode Switcher Icon
                IconButton(
                    onClick = onOpenSwitchMode,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Life360DarkSurface)
                        .border(1.dp, Life360DarkBorder, CircleShape)
                        .testTag("profile_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile & Settings",
                        tint = Life360PurpleLight,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Member Avatar Bubbles Horizontal Row (Life360 Style)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // User Avatar Bubble
            MemberAvatarBubble(
                name = userName,
                roleLabel = "You",
                batteryLevel = batteryState.level,
                isCharging = batteryState.isCharging,
                isSafe = true,
                badgeColor = Life360Purple,
                modifier = Modifier.weight(1f)
            )

            // Paired Parent / Child Avatar Bubble
            MemberAvatarBubble(
                name = pairedName,
                roleLabel = if (role == UserRole.CHILD) "Parent" else "Child",
                batteryLevel = if (role == UserRole.CHILD) 92 else batteryState.level,
                isCharging = false,
                isSafe = true,
                badgeColor = Life360Green,
                modifier = Modifier.weight(1f)
            )

            // Nagpur Police Emergency 112 Bubble
            MemberAvatarBubble(
                name = "Nagpur 112",
                roleLabel = "Police",
                batteryLevel = 100,
                isCharging = true,
                isSafe = true,
                badgeColor = Life360Blue,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun MemberAvatarBubble(
    name: String,
    roleLabel: String,
    batteryLevel: Int,
    isCharging: Boolean,
    isSafe: Boolean,
    badgeColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Life360DarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, Life360DarkBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Avatar Circle with status outline
            Box(
                contentAlignment = Alignment.BottomEnd
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(badgeColor.copy(alpha = 0.8f), badgeColor)
                            )
                        )
                        .border(2.dp, if (isSafe) Life360Green else Life360Red, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = name.take(1).uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                // Battery Badge
                Surface(
                    shape = CircleShape,
                    color = Life360DarkBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Life360DarkBorder),
                    modifier = Modifier.offset(x = 4.dp, y = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        if (isCharging) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = Life360Green,
                                modifier = Modifier.size(8.dp)
                            )
                        }
                        Text(
                            text = "$batteryLevel%",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (batteryLevel > 20) Life360Green else Life360Red
                        )
                    }
                }
            }

            Text(
                text = name,
                color = Life360TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = roleLabel,
                color = Life360TextSecondary,
                fontSize = 9.sp
            )
        }
    }
}
