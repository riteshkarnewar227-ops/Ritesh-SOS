package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole {
    CHILD,
    PARENT,
    POLICE;

    fun getDisplayName(): String = when (this) {
        CHILD -> "User / Child Mode"
        PARENT -> "Parent / Guardian Mode"
        POLICE -> "Police Command Mode"
    }

    fun getBadgeTitle(): String = when (this) {
        CHILD -> "Citizen / Child"
        PARENT -> "Guardian"
        POLICE -> "Police Officer"
    }
}

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Long = 1L,
    val name: String,
    val phoneNumber: String,
    val role: UserRole = UserRole.CHILD,
    val pairingCode: String = "",
    val pairedWithCode: String = "",
    val pairedPersonName: String = "",
    val pairedPersonPhone: String = "",
    val policeStationOrBadge: String = "",
    val isRegistered: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
