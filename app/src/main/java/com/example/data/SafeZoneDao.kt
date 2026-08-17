package com.example.data

import androidx.room.*
import com.example.model.SafeZone
import kotlinx.coroutines.flow.Flow

@Dao
interface SafeZoneDao {
    @Query("SELECT * FROM safe_zones ORDER BY id ASC")
    fun getAllSafeZones(): Flow<List<SafeZone>>

    @Query("SELECT * FROM safe_zones WHERE category = :category")
    fun getSafeZonesByCategory(category: String): Flow<List<SafeZone>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(zones: List<SafeZone>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertZone(zone: SafeZone): Long

    @Query("SELECT COUNT(*) FROM safe_zones")
    suspend fun getCount(): Int
}
