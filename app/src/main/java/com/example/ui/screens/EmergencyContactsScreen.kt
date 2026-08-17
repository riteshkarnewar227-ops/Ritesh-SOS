package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.EmergencyContact
import com.example.ui.theme.*

@Composable
fun EmergencyContactsScreen(
    contacts: List<EmergencyContact>,
    onSaveContact: (EmergencyContact) -> Unit,
    onDeleteContact: (EmergencyContact) -> Unit,
    onTestWhatsApp: (EmergencyContact) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddEditDialog by remember { mutableStateOf(false) }
    var editingContact by remember { mutableStateOf<EmergencyContact?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Life360DarkBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Header
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
                        imageVector = Icons.Default.ContactPhone,
                        contentDescription = null,
                        tint = Life360PurpleLight,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "SAFETY CIRCLE CONTACTS",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }
                Text(
                    text = "Parents & Police WhatsApp / SMS Receivers",
                    color = Life360TextSecondary,
                    fontSize = 11.sp
                )
            }

            // Add Contact Button
            Button(
                onClick = {
                    editingContact = null
                    showAddEditDialog = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = Life360Purple),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Contact",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Member", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        // WhatsApp Direct Notice Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, Life360Green.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = Life360DarkSurface)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(Life360Green.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = null,
                        tint = Life360Green,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Automated WhatsApp SOS Dispatch Ready",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "When SOS is triggered, Google Maps live location & battery stats are sent immediately to Parent & Police WhatsApp.",
                        color = Life360TextSecondary,
                        fontSize = 10.sp
                    )
                }
            }
        }

        // Contacts List
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(contacts) { contact ->
                ContactCard(
                    contact = contact,
                    onEdit = {
                        editingContact = contact
                        showAddEditDialog = true
                    },
                    onDelete = { onDeleteContact(contact) },
                    onTestWhatsApp = { onTestWhatsApp(contact) }
                )
            }
        }
    }

    if (showAddEditDialog) {
        ContactEditDialog(
            initialContact = editingContact,
            onDismiss = { showAddEditDialog = false },
            onSave = { contact ->
                onSaveContact(contact)
                showAddEditDialog = false
            }
        )
    }
}

@Composable
private fun ContactCard(
    contact: EmergencyContact,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onTestWhatsApp: () -> Unit
) {
    val relColor = when (contact.relationship) {
        "Parent" -> Life360Green
        "Police" -> Life360Blue
        else -> Life360PurpleLight
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                1.dp,
                if (contact.isPrimary) Life360Purple else Life360DarkBorder,
                RoundedCornerShape(16.dp)
            )
            .testTag("contact_card_${contact.id}"),
        colors = CardDefaults.cardColors(containerColor = Life360DarkSurface)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(relColor.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (contact.relationship) {
                                "Parent" -> Icons.Default.FamilyRestroom
                                "Police" -> Icons.Default.LocalPolice
                                else -> Icons.Default.Person
                            },
                            contentDescription = null,
                            tint = relColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = contact.name,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            if (contact.isPrimary) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Life360Purple.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "PRIMARY",
                                        color = Life360PurpleLight,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            text = "${contact.phoneNumber} • ${contact.relationship}",
                            color = Life360TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Life360PurpleLight,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Life360Red,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            if (contact.customNotes.isNotBlank()) {
                Text(
                    text = "📝 ${contact.customNotes}",
                    color = Life360TextSecondary,
                    fontSize = 11.sp
                )
            }

            // Test WhatsApp Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (contact.isWhatsAppEnabled) "✅ WhatsApp Active" else "❌ WhatsApp Off",
                        color = if (contact.isWhatsAppEnabled) Life360Green else Life360TextMuted,
                        fontSize = 11.sp
                    )
                }

                Button(
                    onClick = onTestWhatsApp,
                    colors = ButtonDefaults.buttonColors(containerColor = Life360Green),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Test WhatsApp", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ContactEditDialog(
    initialContact: EmergencyContact?,
    onDismiss: () -> Unit,
    onSave: (EmergencyContact) -> Unit
) {
    var name by remember { mutableStateOf(initialContact?.name ?: "") }
    var phone by remember { mutableStateOf(initialContact?.phoneNumber ?: "") }
    var relationship by remember { mutableStateOf(initialContact?.relationship ?: "Parent") }
    var isWhatsApp by remember { mutableStateOf(initialContact?.isWhatsAppEnabled ?: true) }
    var isPrimary by remember { mutableStateOf(initialContact?.isPrimary ?: false) }
    var notes by remember { mutableStateOf(initialContact?.customNotes ?: "") }

    val relOptions = listOf("Parent", "Police", "Relative", "Friend", "Guardian")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Life360DarkSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, Life360DarkBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (initialContact == null) "ADD EMERGENCY CONTACT" else "EDIT CONTACT",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name / Role") },
                    placeholder = { Text("e.g. Mom / Dad / Local Police") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Life360Purple,
                        unfocusedBorderColor = Life360DarkBorder
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("WhatsApp Phone Number") },
                    placeholder = { Text("e.g. +919876543210 or 112") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Life360Purple,
                        unfocusedBorderColor = Life360DarkBorder
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Relationship selection
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Relationship Category:",
                        color = Life360TextSecondary,
                        fontSize = 11.sp
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        relOptions.take(3).forEach { option ->
                            val isSel = relationship == option
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSel) Life360PurpleDark else Life360DarkSurfaceElevated,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSel) Life360Purple else Life360DarkBorder
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { relationship = option }
                            ) {
                                Text(
                                    text = option,
                                    color = if (isSel) Color.White else Life360TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // Switches
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "WhatsApp Emergency Broadcast", color = Color.White, fontSize = 12.sp)
                    Switch(
                        checked = isWhatsApp,
                        onCheckedChange = { isWhatsApp = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Life360Green)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Mark as Primary Guardian", color = Color.White, fontSize = 12.sp)
                    Switch(
                        checked = isPrimary,
                        onCheckedChange = { isPrimary = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Life360Purple)
                    )
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel", color = Life360TextSecondary)
                    }

                    Button(
                        onClick = {
                            if (name.isNotBlank() && phone.isNotBlank()) {
                                onSave(
                                    EmergencyContact(
                                        id = initialContact?.id ?: 0L,
                                        name = name,
                                        phoneNumber = phone,
                                        relationship = relationship,
                                        isWhatsAppEnabled = isWhatsApp,
                                        isPrimary = isPrimary,
                                        customNotes = notes
                                    )
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Life360Purple),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save Member", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

