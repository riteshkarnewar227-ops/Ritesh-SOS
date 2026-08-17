package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserRole
import com.example.ui.theme.*

@Composable
fun SignUpScreen(
    onCompleteSignUp: (name: String, phone: String, role: UserRole, extraField: String, pairedPhone: String) -> Unit,
    initialRole: UserRole = UserRole.CHILD,
    isSwitchingMode: Boolean = false,
    onCancelSwitch: (() -> Unit)? = null
) {
    var selectedRole by remember { mutableStateOf(initialRole) }
    var name by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var extraField by remember { mutableStateOf("") }
    var pairedPhone by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Life360DarkBg)
            .verticalScroll(scrollState)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Header Title
        Box(
            modifier = Modifier
                .size(68.dp)
                .background(
                    when (selectedRole) {
                        UserRole.CHILD -> Life360Purple.copy(alpha = 0.2f)
                        UserRole.PARENT -> Life360Green.copy(alpha = 0.2f)
                        UserRole.POLICE -> Life360Blue.copy(alpha = 0.2f)
                    },
                    CircleShape
                )
                .border(
                    2.dp,
                    when (selectedRole) {
                        UserRole.CHILD -> Life360Purple
                        UserRole.PARENT -> Life360Green
                        UserRole.POLICE -> Life360Blue
                    },
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = when (selectedRole) {
                    UserRole.CHILD -> Icons.Default.Groups
                    UserRole.PARENT -> Icons.Default.FamilyRestroom
                    UserRole.POLICE -> Icons.Default.LocalPolice
                },
                contentDescription = null,
                tint = when (selectedRole) {
                    UserRole.CHILD -> Life360PurpleLight
                    UserRole.PARENT -> Life360Green
                    UserRole.POLICE -> Life360Blue
                },
                modifier = Modifier.size(36.dp)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (isSwitchingMode) "SWITCH CIRCLE ROLE" else "NAGPUR SURAKSHA 360",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
            Text(
                text = if (isSwitchingMode) "Select your role to switch circle view" else "Family Safety Circle & Automated Emergency Dispatch",
                color = Life360TextSecondary,
                fontSize = 12.sp
            )
        }

        // 3 Role Selection Cards
        Text(
            text = "STEP 1: SELECT YOUR ACCOUNT MODE",
            color = Life360TextSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.align(Alignment.Start)
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            RoleOptionCard(
                title = "Member / Child Mode",
                subtitle = "For students, commuters & citizens needing 1-tap SOS, live GPS sharing, and family code pairing.",
                icon = Icons.Default.PersonPinCircle,
                badgeColor = Life360Purple,
                isSelected = selectedRole == UserRole.CHILD,
                onClick = { selectedRole = UserRole.CHILD }
            )

            RoleOptionCard(
                title = "Parent / Guardian Mode",
                subtitle = "For parents & guardians to pair with members, track live GPS & battery telemetry, and receive urgent WhatsApp SOS.",
                icon = Icons.Default.FamilyRestroom,
                badgeColor = Life360Green,
                isSelected = selectedRole == UserRole.PARENT,
                onClick = { selectedRole = UserRole.PARENT }
            )

            RoleOptionCard(
                title = "Police Command Mode",
                subtitle = "For Nagpur Police officers to monitor active citizen distress signals, live GPS radar, and audio evidence.",
                icon = Icons.Default.LocalPolice,
                badgeColor = Life360Blue,
                isSelected = selectedRole == UserRole.POLICE,
                onClick = { selectedRole = UserRole.POLICE }
            )
        }

        // Step 2: Information Inputs
        Text(
            text = "STEP 2: ENTER YOUR DETAILS",
            color = Life360TextSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.align(Alignment.Start)
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
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Name Field
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; errorMessage = "" },
                    label = {
                        Text(
                            when (selectedRole) {
                                UserRole.CHILD -> "Your Name / Member Name"
                                UserRole.PARENT -> "Parent / Guardian Name"
                                UserRole.POLICE -> "Officer Name / Rank"
                            }
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Life360PurpleLight
                        )
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Life360Purple,
                        unfocusedBorderColor = Life360DarkBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("signup_name_input")
                )

                // Phone Number Field
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it; errorMessage = "" },
                    label = { Text("Your Mobile Number (WhatsApp)") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            tint = Life360PurpleLight
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Life360Purple,
                        unfocusedBorderColor = Life360DarkBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("signup_phone_input")
                )

                // Role specific input
                when (selectedRole) {
                    UserRole.CHILD -> {
                        OutlinedTextField(
                            value = pairedPhone,
                            onValueChange = { pairedPhone = it },
                            label = { Text("Parent's Mobile Number (Optional)") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.ContactPhone,
                                    contentDescription = null,
                                    tint = Life360Green
                                )
                            },
                            placeholder = { Text("e.g. 9876543210") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Life360Green,
                                unfocusedBorderColor = Life360DarkBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("signup_parent_phone_input")
                        )
                    }

                    UserRole.PARENT -> {
                        OutlinedTextField(
                            value = extraField,
                            onValueChange = { extraField = it },
                            label = { Text("Member's Pairing Code (Optional, e.g. SUR-1234)") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.QrCode,
                                    contentDescription = null,
                                    tint = Life360PurpleLight
                                )
                            },
                            placeholder = { Text("e.g. SUR-8492 (or connect later)") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Life360Purple,
                                unfocusedBorderColor = Life360DarkBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
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
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Badge,
                                    contentDescription = null,
                                    tint = Life360Blue
                                )
                            },
                            placeholder = { Text("e.g. Sitabuldi Police Station / Badge #402") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Life360Blue,
                                unfocusedBorderColor = Life360DarkBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
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
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
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
                onCompleteSignUp(
                    name.trim(),
                    phoneNumber.trim(),
                    selectedRole,
                    extraField.trim(),
                    pairedPhone.trim()
                )
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = when (selectedRole) {
                    UserRole.CHILD -> Life360Purple
                    UserRole.PARENT -> Life360Green
                    UserRole.POLICE -> Life360Blue
                }
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("complete_signup_button")
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isSwitchingMode) "Switch to ${selectedRole.getBadgeTitle()} Mode" else "Complete Sign Up & Launch App",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (isSwitchingMode && onCancelSwitch != null) {
            TextButton(
                onClick = onCancelSwitch,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel & Return to Dashboard", color = Life360TextSecondary)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun RoleOptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    badgeColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                2.dp,
                if (isSelected) badgeColor else Life360DarkBorder,
                RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .testTag("role_option_${title.replace(" ", "_").lowercase()}"),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Life360DarkSurfaceElevated else Life360DarkSurface
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(badgeColor.copy(alpha = 0.25f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = badgeColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = Life360TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }

            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = badgeColor,
                    unselectedColor = Life360DarkBorder
                )
            )
        }
    }
}

