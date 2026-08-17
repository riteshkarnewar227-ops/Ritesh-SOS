package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserProfile
import com.example.model.UserRole
import com.example.ui.theme.*

@Composable
fun ProfileDialog(
    userProfile: UserProfile?,
    onSwitchRole: (UserRole) -> Unit,
    onResetProfile: () -> Unit,
    onDismiss: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    var copiedCode by remember { mutableStateOf(false) }
    var copiedPasskey by remember { mutableStateOf(false) }
    var pendingRoleToSwitch by remember { mutableStateOf<UserRole?>(null) }
    var showPasskeyAuth by remember { mutableStateOf(false) }

    if (showPasskeyAuth && pendingRoleToSwitch != null) {
        PasskeyAuthDialog(
            targetActionTitle = "${pendingRoleToSwitch?.getBadgeTitle()} Dashboard",
            targetRole = pendingRoleToSwitch,
            userProfile = userProfile,
            onAuthorized = {
                val target = pendingRoleToSwitch ?: UserRole.CHILD
                showPasskeyAuth = false
                pendingRoleToSwitch = null
                onSwitchRole(target)
                onDismiss()
            },
            onDismiss = {
                showPasskeyAuth = false
                pendingRoleToSwitch = null
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            when (userProfile?.role) {
                                UserRole.CHILD -> Life360PurpleBg
                                UserRole.PARENT -> Life360GreenBg
                                UserRole.POLICE -> Life360Indigo.copy(alpha = 0.12f)
                                null -> Life360PurpleBg
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint = when (userProfile?.role) {
                            UserRole.CHILD -> Life360PurpleDark
                            UserRole.PARENT -> Life360Green
                            UserRole.POLICE -> Life360Indigo
                            null -> Life360PurpleDark
                        },
                        modifier = Modifier.size(24.dp)
                    )
                }
                Column {
                    Text(
                        text = "Dashboard & Circle Mode",
                        color = Life360TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Current: ${userProfile?.role?.getDisplayName() ?: "Member"}",
                        color = Life360TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Profile & Security Details Card
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Life360LightSurfaceElevated,
                    border = BorderStroke(1.dp, Life360LightBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ProfileInfoRow(label = "Name", value = userProfile?.name ?: "Member")
                        ProfileInfoRow(label = "Phone", value = userProfile?.phoneNumber ?: "Not set")

                        // Pairing Code Row with Copy
                        if (!userProfile?.pairingCode.isNullOrBlank()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Family Pairing Code", color = Life360TextSecondary, fontSize = 11.sp)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = userProfile?.pairingCode ?: "",
                                        color = Life360PurpleDark,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    IconButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(userProfile?.pairingCode ?: ""))
                                            copiedCode = true
                                        },
                                        modifier = Modifier.size(22.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (copiedCode) Icons.Default.Check else Icons.Default.ContentCopy,
                                            contentDescription = "Copy Code",
                                            tint = if (copiedCode) Life360Green else Life360Purple,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Passkey Info Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Security Passkey", color = Life360TextSecondary, fontSize = 11.sp)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = userProfile?.passkey?.ifBlank { "1234" } ?: "1234",
                                    color = Life360Green,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                IconButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(userProfile?.passkey?.ifBlank { "1234" } ?: "1234"))
                                        copiedPasskey = true
                                    },
                                    modifier = Modifier.size(22.dp)
                                ) {
                                    Icon(
                                        imageVector = if (copiedPasskey) Icons.Default.Check else Icons.Default.ContentCopy,
                                        contentDescription = "Copy Passkey",
                                        tint = if (copiedPasskey) Life360Green else Life360Purple,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Switch Mode Selector (Locked with Passkey)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SWITCH DASHBOARDS:",
                        color = Life360TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Life360Purple,
                            modifier = Modifier.size(10.dp)
                        )
                        Text(
                            text = "Requires Passkey",
                            color = Life360PurpleDark,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    ModeSwitchItem(
                        role = UserRole.CHILD,
                        title = "Member / Child Mode",
                        subtitle = "SOS trigger, safe routes & GPS",
                        isSelected = userProfile?.role == UserRole.CHILD,
                        onSelect = {
                            if (userProfile?.role == UserRole.CHILD) {
                                onDismiss()
                            } else {
                                pendingRoleToSwitch = UserRole.CHILD
                                showPasskeyAuth = true
                            }
                        }
                    )
                    ModeSwitchItem(
                        role = UserRole.PARENT,
                        title = "Parent / Guardian Mode",
                        subtitle = "Ward tracking & battery telemetry",
                        isSelected = userProfile?.role == UserRole.PARENT,
                        onSelect = {
                            if (userProfile?.role == UserRole.PARENT) {
                                onDismiss()
                            } else {
                                pendingRoleToSwitch = UserRole.PARENT
                                showPasskeyAuth = true
                            }
                        }
                    )
                    ModeSwitchItem(
                        role = UserRole.POLICE,
                        title = "Police Command Mode",
                        subtitle = "Official citizen distress radar & dispatch",
                        isSelected = userProfile?.role == UserRole.POLICE,
                        onSelect = {
                            if (userProfile?.role == UserRole.POLICE) {
                                onDismiss()
                            } else {
                                pendingRoleToSwitch = UserRole.POLICE
                                showPasskeyAuth = true
                            }
                        }
                    )
                }

                // Reset / Logout option
                TextButton(
                    onClick = {
                        onResetProfile()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = null,
                        tint = Life360Red,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Sign Out & Switch Account",
                        color = Life360Red,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Life360Purple)
            ) {
                Text("Done", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    )
}

@Composable
private fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = Life360TextSecondary, fontSize = 11.sp)
        Text(text = value, color = Life360TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ModeSwitchItem(
    role: UserRole,
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Surface(
        color = if (isSelected) Life360PurpleBg else Life360LightSurface,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            1.dp,
            if (isSelected) Life360Purple else Life360LightBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag("switch_to_${role.name.lowercase()}")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = title,
                    color = if (isSelected) Life360PurpleDark else Life360TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    color = Life360TextSecondary,
                    fontSize = 10.sp
                )
            }
            if (isSelected) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Life360Green.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "Active",
                        color = Life360Green,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked with Passkey",
                    tint = Life360TextSecondary,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}
