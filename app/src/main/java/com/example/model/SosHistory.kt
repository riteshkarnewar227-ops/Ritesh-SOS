package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sos_history")
data class SosHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val latitude: Double,
    val longitude: Double,
    val accuracyMeters: Float = 0f,
    val address: String = "",
    val batteryPercent: Int = 100,
    val isCharging: Boolean = false,
    val contactsAlerted: String = "", // e.g. "Parent (Mom), Nagpur Police 112"
    val status: String = "SENT_WHATSAPP", // "SENT_WHATSAPP", "SENT_SMS", "QUEUED_OFFLINE"
    val googleMapsUrl: String = "",
    val audioFilePath: String = "",
    val hasAudioRecording: Boolean = false,
    val senderName: String = "",
    val pairingCode: String = ""
)
