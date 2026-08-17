package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emergency_contacts")
data class EmergencyContact(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phoneNumber: String,
    val relationship: String, // "Parent", "Police", "Relative", "Friend", "Guardian"
    val isWhatsAppEnabled: Boolean = true,
    val isSmsEnabled: Boolean = true,
    val isPrimary: Boolean = false,
    val customNotes: String = ""
)
