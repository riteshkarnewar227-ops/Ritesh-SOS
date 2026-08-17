package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserRole
import com.example.ui.theme.*

@Composable
fun SignUpScreen(
    onCompleteSignUp: (name: String, phone: String, role: UserRole, passkey: String, extraField: String, pairedPhone: String) -> Unit,
    initialRole: UserRole = UserRole.CHILD,
    isSwitchingMode: Boolean = false,
    onCancelSwitch: (() -> Unit)? = null
) {
    var selectedRole by remember { mutableStateOf(initialRole) }
    var name by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var passkey by remember { mutableStateOf("1234") }
    var isPasskeyVisible by remember { mutableStateOf(false) }
    var extraField by remember { mutableStateOf("") }
    var pairedPhone by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Life360LightBg)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Header Brand
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(
                    when (selectedRole) {
                        UserRole.CHILD -> Life360PurpleBg
                        UserRole.PARENT -> Life360GreenBg
                        UserRole.POLICE -> Life360Indigo.copy(alpha = 0.12f)
                    }
                )
                .border(
                    2.dp,
                    when (selectedRole) {
                        UserRole.CHILD -> Life360Purple
                        UserRole.PARENT -> Life360Green
                        UserRole.POLICE -> Life360Indigo
                    },
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = when (selectedRole) {
                    UserRole.CHILD -> Icons.Default.PersonPinCircle
                    UserRole.PARENT -> Icons.Default.FamilyRestroom
                    UserRole.POLICE -> Icons.Default.LocalPolice
                },
                contentDescription = null,
                tint = when (selectedRole) {
                    UserRole.CHILD -> Life360PurpleDark
                    UserRole.PARENT -> Life360Green
                    UserRole.POLICE -> Life360Indigo
                },
                modifier = Modifier.size(32.dp)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (isSwitchingMode) "SWITCH DASHBOARD MODE" else "SURAKSHA 360",
                color = Life360TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
            Text(
                text = if (isSwitchingMode) "Enter Passkey to authenticate mode switch" else "Secure Family Protection & Emergency Dispatch",
                color = Life360TextSecondary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }

        // Step 1: Select Dedicated Role Mode
        Text(
            text = "STEP 1: SELECT YOUR DEDICATED MODE",
            color = Life360TextMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
            modifier = Modifier.align(Alignment.Start)
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RoleOptionTile(
                title = "Member / Child Mode",
                subtitle = "Single-purpose SOS screen, safe routes, live GPS, distress audio & family pairing code.",
                icon = Icons.Default.PersonPinCircle,
                accentColor = Life360Purple,
                isSelected = selectedRole == UserRole.CHILD,
                onClick = { selectedRole = UserRole.CHILD }
            )

            RoleOptionTile(
                title = "Parent / Guardian Mode",
                subtitle = "Dedicated guardian dashboard with live member battery, location tracking & safe places.",
                icon = Icons.Default.FamilyRestroom,
                accentColor = Life360Green,
                isSelected = selectedRole == UserRole.PARENT,
                onClick = { selectedRole = UserRole.PARENT }
            )

            RoleOptionTile(
                title = "Police Command Mode",
                subtitle = "Official Nagpur Police incident radar, citizen dispatch & emergency audio log verification.",
                icon = Icons.Default.LocalPolice,
                accentColor = Life360Indigo,
                isSelected = selectedRole == UserRole.POLICE,
                onClick = { selectedRole = UserRole.POLICE }
            )
        }

        // Step 2: Information & Passkey
        Text(
            text = "STEP 2: DETAILS & SECURITY PASSKEY",
            color = Life360TextMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
            modifier = Modifier.align(Alignment.Start)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, Life360LightBorder, RoundedCornerShape(20.dp))
                .shadow(2.dp, RoundedCornerShape(20.dp), ambientColor = Life360PurpleBg),
            colors = CardDefaults.cardColors(containerColor = Life360LightSurface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Name Field
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; errorMessage = "" },
                    label = {
                        Text(
                            when (selectedRole) {
                                UserRole.CHILD -> "Your Name (e.g. Priya Sharma)"
                                UserRole.PARENT -> "Guardian Name (e.g. Rajesh Sharma)"
                                UserRole.POLICE -> "Officer Name / Rank"
                            }
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Life360Purple
                        )
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Life360Purple,
                        unfocusedBorderColor = Life360LightBorder,
                        focusedTextColor = Life360TextPrimary,
                        unfocusedTextColor = Life360TextPrimary,
                        focusedContainerColor = Life360LightSurfaceElevated,
                        unfocusedContainerColor = Life360LightSurfaceElevated
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("signup_name_input")
                )

                // Phone Number Field
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it; errorMessage = "" },
                    label = { Text("Your Mobile Number (WhatsApp)") },
                    placeholder = { Text("+91 9876543210") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            tint = Life360Purple
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Life360Purple,
                        unfocusedBorderColor = Life360LightBorder,
                        focusedTextColor = Life360TextPrimary,
                        unfocusedTextColor = Life360TextPrimary,
                        focusedContainerColor = Life360LightSurfaceElevated,
                        unfocusedContainerColor = Life360LightSurfaceElevated
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("signup_phone_input")
                )

                // Security Passkey / PIN Field
                OutlinedTextField(
                    value = passkey,
                    onValueChange = {
                        if (it.length <= 8) {
                            passkey = it
                            errorMessage = ""
                        }
                    },
                    label = { Text("Security Passkey / PIN (4-6 digits)") },
                    placeholder = { Text("1234") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Life360Purple
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { isPasskeyVisible = !isPasskeyVisible }) {
                            Icon(
                                imageVector = if (isPasskeyVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle Visibility",
                                tint = Life360TextSecondary
                            )
                        }
                    },
                    visualTransformation = if (isPasskeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Life360Purple,
                        unfocusedBorderColor = Life360LightBorder,
                        focusedTextColor = Life360TextPrimary,
                        unfocusedTextColor = Life360TextPrimary,
                        focusedContainerColor = Life360LightSurfaceElevated,
                        unfocusedContainerColor = Life360LightSurfaceElevated
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("signup_passkey_input")
                )

                // Security Passkey Helper Note
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Life360PurpleBg,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = Life360PurpleDark,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Dashboards connect only via this Passkey. Default: 1234",
                            color = Life360PurpleDark,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Role-specific fields
                when (selectedRole) {
                    UserRole.CHILD -> {
                        OutlinedTextField(
                            value = pairedPhone,
                            onValueChange = { pairedPhone = it },
                            label = { Text("Parent's Mobile Number (Optional)") },
                            placeholder = { Text("e.g. 9876543210") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.ContactPhone,
                                    contentDescription = null,
                                    tint = Life360Green
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Life360Green,
                                unfocusedBorderColor = Life360LightBorder,
                                focusedTextColor = Life360TextPrimary,
                                unfocusedTextColor = Life360TextPrimary,
                                focusedContainerColor = Life360LightSurfaceElevated,
                                unfocusedContainerColor = Life360LightSurfaceElevated
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("signup_parent_phone_input")
                        )
                    }

                    UserRole.PARENT -> {
                        OutlinedTextField(
                            value = extraField,
                            onValueChange = { extraField = it.uppercase() },
                            label = { Text("Member's Code (Optional, e.g. SUR-1234)") },
                            placeholder = { Text("Enter code shown on member screen") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.QrCode,
                                    contentDescription = null,
                                    tint = Life360Green
                                )
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Life360Green,
                                unfocusedBorderColor = Life360LightBorder,
                                focusedTextColor = Life360TextPrimary,
                                unfocusedTextColor = Life360TextPrimary,
                                focusedContainerColor = Life360LightSurfaceElevated,
                                unfocusedContainerColor = Life360LightSurfaceElevated
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("signup_child_code_input")
                        )
                    }

                    UserRole.POLICE -> {
                        OutlinedTextField(
                            value = extraField,
                            onValueChange = { extraField = it },
                            label = { Text("Police Station / Badge ID") },
                            placeholder = { Text("e.g. Sitabuldi Station / Badge #402") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Badge,
                                    contentDescription = null,
                                    tint = Life360Indigo
                                )
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Life360Indigo,
                                unfocusedBorderColor = Life360LightBorder,
                                focusedTextColor = Life360TextPrimary,
                                unfocusedTextColor = Life360TextPrimary,
                                focusedContainerColor = Life360LightSurfaceElevated,
                                unfocusedContainerColor = Life360LightSurfaceElevated
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("signup_police_station_input")
                        )
                    }
                }

                if (errorMessage.isNotBlank()) {
                    Text(
                        text = errorMessage,
                        color = Life360Red,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Submit Button
        Button(
            onClick = {
                if (name.isBlank()) {
                    errorMessage = "Please enter your name"
                    return@Button
                }
                if (phoneNumber.isBlank() || phoneNumber.length < 5) {
                    errorMessage = "Please enter a valid phone number"
                    return@Button
                }
                if (passkey.isBlank() || passkey.length < 4) {
                    errorMessage = "Please set a security passkey of at least 4 digits"
                    return@Button
                }
                onCompleteSignUp(
                    name.trim(),
                    phoneNumber.trim(),
                    selectedRole,
                    passkey.trim(),
                    extraField.trim(),
                    pairedPhone.trim()
                )
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = when (selectedRole) {
                    UserRole.CHILD -> Life360Purple
                    UserRole.PARENT -> Life360Green
                    UserRole.POLICE -> Life360Indigo
                }
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("complete_signup_button")
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isSwitchingMode) "Authorize & Launch Dashboard" else "Launch Dedicated ${selectedRole.getBadgeTitle()} Dashboard",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (isSwitchingMode && onCancelSwitch != null) {
            TextButton(
                onClick = onCancelSwitch,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel & Return", color = Life360TextSecondary, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun RoleOptionTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) accentColor.copy(alpha = 0.08f) else Life360LightSurface,
        border = BorderStroke(
            if (isSelected) 2.dp else 1.dp,
            if (isSelected) accentColor else Life360LightBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("role_option_${title.replace(" ", "_").lowercase()}")
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Life360TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = Life360TextSecondary,
                    fontSize = 10.sp,
                    lineHeight = 13.sp
                )
            }

            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = accentColor,
                    unselectedColor = Life360LightBorder
                )
            )
        }
    }
}
