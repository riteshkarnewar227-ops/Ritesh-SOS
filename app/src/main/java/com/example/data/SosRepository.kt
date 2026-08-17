package com.example.data

import com.example.model.EmergencyContact
import com.example.model.SafeZone
import com.example.model.SosHistory
import com.example.model.UserProfile
import kotlinx.coroutines.flow.Flow

class SosRepository(private val database: AppDatabase) {
    val contacts: Flow<List<EmergencyContact>> = database.contactDao().getAllContacts()
    val sosHistory: Flow<List<SosHistory>> = database.sosHistoryDao().getAllHistory()
    val safeZones: Flow<List<SafeZone>> = database.safeZoneDao().getAllSafeZones()
    val userProfile: Flow<UserProfile?> = database.userProfileDao().getProfile()

    suspend fun getProfileSync(): UserProfile? = database.userProfileDao().getProfileSync()

    suspend fun saveProfile(profile: UserProfile) {
        database.userProfileDao().saveProfile(profile)
    }

    suspend fun updatePairing(code: String, name: String, phone: String) {
        database.userProfileDao().updatePairing(code, name, phone)
    }

    suspend fun clearProfile() {
        database.userProfileDao().clearProfile()
    }

    suspend fun isUserRegistered(): Boolean {
        return database.userProfileDao().getRegisteredCount() > 0
    }

    suspend fun getWhatsAppContacts(): List<EmergencyContact> =
        database.contactDao().getWhatsAppContacts()

    suspend fun getParentContacts(): List<EmergencyContact> =
        database.contactDao().getParentContacts()

    suspend fun getPoliceContacts(): List<EmergencyContact> =
        database.contactDao().getPoliceContacts()

    suspend fun saveContact(contact: EmergencyContact): Long {
        return if (contact.id == 0L) {
            database.contactDao().insertContact(contact)
        } else {
            database.contactDao().updateContact(contact)
            contact.id
        }
    }

    suspend fun deleteContact(contact: EmergencyContact) =
        database.contactDao().deleteContact(contact)

    suspend fun logSosEvent(history: SosHistory): Long =
        database.sosHistoryDao().insertHistory(history)

    suspend fun clearHistory() =
        database.sosHistoryDao().clearAll()

    suspend fun addSafeZone(zone: SafeZone): Long =
        database.safeZoneDao().insertZone(zone)

    suspend fun ensureSeeded() {
        AppDatabase.populateInitialData(database)
    }
}
