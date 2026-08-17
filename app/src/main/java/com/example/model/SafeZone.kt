package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "safe_zones")
data class SafeZone(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String, // "Police Station", "Hospital", "Women Safety Booth", "Metro Station"
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val phoneNumber: String,
    val is24Hours: Boolean = true
)
