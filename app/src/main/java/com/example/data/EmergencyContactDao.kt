package com.example.data

import androidx.room.*
import com.example.model.EmergencyContact
import kotlinx.coroutines.flow.Flow

@Dao
interface EmergencyContactDao {
    @Query("SELECT * FROM emergency_contacts ORDER BY isPrimary DESC, id ASC")
    fun getAllContacts(): Flow<List<EmergencyContact>>

    @Query("SELECT * FROM emergency_contacts WHERE isWhatsAppEnabled = 1")
    suspend fun getWhatsAppContacts(): List<EmergencyContact>

    @Query("SELECT * FROM emergency_contacts WHERE relationship = 'Parent' OR relationship = 'Guardian'")
    suspend fun getParentContacts(): List<EmergencyContact>

    @Query("SELECT * FROM emergency_contacts WHERE relationship = 'Police'")
    suspend fun getPoliceContacts(): List<EmergencyContact>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: EmergencyContact): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(contacts: List<EmergencyContact>)

    @Update
    suspend fun updateContact(contact: EmergencyContact)

    @Delete
    suspend fun deleteContact(contact: EmergencyContact)

    @Query("SELECT COUNT(*) FROM emergency_contacts")
    suspend fun getCount(): Int
}
