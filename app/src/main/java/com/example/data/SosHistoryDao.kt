package com.example.data

import androidx.room.*
import com.example.model.SosHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface SosHistoryDao {
    @Query("SELECT * FROM sos_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<SosHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: SosHistory): Long

    @Query("DELETE FROM sos_history")
    suspend fun clearAll()
}
