package com.example.data

import androidx.room.*
import com.example.model.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getProfile(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getProfileSync(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProfile(profile: UserProfile)

    @Query("UPDATE user_profile SET pairedWithCode = :code, pairedPersonName = :name, pairedPersonPhone = :phone WHERE id = 1")
    suspend fun updatePairing(code: String, name: String, phone: String)

    @Query("DELETE FROM user_profile")
    suspend fun clearProfile()

    @Query("SELECT COUNT(*) FROM user_profile WHERE isRegistered = 1")
    suspend fun getRegisteredCount(): Int
}
