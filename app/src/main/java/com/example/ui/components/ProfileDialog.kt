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
    var copied by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(
                            when (userProfile?.role) {
                                UserRole.CHILD -> Life360Purple.copy(alpha = 0.2f)
                                UserRole.PARENT -> Life360Green.copy(alpha = 0.2f)
                                UserRole.POLICE -> Life360Blue.copy(alpha = 0.2f)
                                null -> Life360Purple.copy(alpha = 0.2f)
                            },
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint = when (userProfile?.role) {
                            UserRole.CHILD -> Life360PurpleLight
                            UserRole.PARENT -> Life360Green
                            UserRole.POLICE -> Life360Blue
                            null -> Life360PurpleLight
                        },
                        modifier = Modifier.size(24.dp)
                    )
                }
                Column {
                    Text(
                        text = "Account & Circle Settings",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Life360 Family Identity",
                        color = Life360TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Profile details card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, Life360DarkBorder, RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = Life360DarkSurfaceElevated)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ProfileInfoRow(label = "Name", value = userProfile?.name ?: "Member")
                        ProfileInfoRow(label = "Phone", value = userProfile?.phoneNumber ?: "Not set")
                        ProfileInfoRow(label = "Current Role", value = userProfile?.role?.getDisplayName() ?: "Member Mode")
                        if (!userProfile?.pairingCode.isNullOrBlank()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ProfileInfoRow(label = "Pairing Code", value = userProfile?.pairingCode ?: "")
                                IconButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(userProfile?.pairingCode ?: ""))
                                        copied = true
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = if (copied) Icons.Default.Check else Icons.Default.ContentCopy,
                                        contentDescription = "Copy",
                                        tint = if (copied) Life360Green else Life360PurpleLight,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Switch Mode Selector
                Text(
                    text = "SWITCH CIRCLE ROLE:",
                    color = Life360TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ModeSwitchOption(
                        role = UserRole.CHILD,
                        title = "Member / Child Mode",
                        isSelected = userProfile?.role == UserRole.CHILD,
                        onSelect = { onSwitchRole(UserRole.CHILD); onDismiss() }
                    )
                    ModeSwitchOption(
                        role = UserRole.PARENT,
                        title = "Parent / Guardian Mode",
                        isSelected = userProfile?.role == UserRole.PARENT,
                        onSelect = { onSwitchRole(UserRole.PARENT); onDismiss() }
                    )
                    ModeSwitchOption(
                        role = UserRole.POLICE,
                        title = "Police Command Mode",
                        isSelected = userProfile?.role == UserRole.POLICE,
                        onSelect = { onSwitchRole(UserRole.POLICE); onDismiss() }
                    )
                }

                // Re-register / Logout option
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
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Reset Profile / Sign Up as New Member",
                        color = Life360Red,
                        fontSize = 12.sp,
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
                Text("Close", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = Life360DarkSurface
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
        Text(text = value, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ModeSwitchOption(
    role: UserRole,
    title: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Surface(
        color = if (isSelected) {
            when (role) {
                UserRole.CHILD -> Life360PurpleDark
                UserRole.PARENT -> Life360Green.copy(alpha = 0.15f)
                UserRole.POLICE -> Life360Blue.copy(alpha = 0.15f)
            }
        } else Life360DarkSurfaceElevated,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) {
                when (role) {
                    UserRole.CHILD -> Life360Purple
                    UserRole.PARENT -> Life360Green
                    UserRole.POLICE -> Life360Blue
                }
            } else Life360DarkBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onSelect() }
            .testTag("switch_to_${role.name.lowercase()}")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                color = if (isSelected) Color.White else Life360TextSecondary,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Life360Green,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

