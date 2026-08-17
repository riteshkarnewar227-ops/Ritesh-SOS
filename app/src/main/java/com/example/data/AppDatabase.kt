package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.model.EmergencyContact
import com.example.model.SafeZone
import com.example.model.SosHistory
import com.example.model.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [EmergencyContact::class, SosHistory::class, SafeZone::class, UserProfile::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactDao(): EmergencyContactDao
    abstract fun sosHistoryDao(): SosHistoryDao
    abstract fun safeZoneDao(): SafeZoneDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "nagpur_suraksha_db"
                ).fallbackToDestructiveMigration()
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        scope.launch(Dispatchers.IO) {
                            INSTANCE?.let { database ->
                                populateInitialData(database)
                            }
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun populateInitialData(database: AppDatabase) {
            val contactDao = database.contactDao()
            if (contactDao.getCount() == 0) {
                contactDao.insertAll(
                    listOf(
                        EmergencyContact(
                            name = "Parent (Primary Guardian)",
                            phoneNumber = "+919876543210",
                            relationship = "Parent",
                            isWhatsAppEnabled = true,
                            isSmsEnabled = true,
                            isPrimary = true,
                            customNotes = "Home Contact / Dad & Mom"
                        ),
                        EmergencyContact(
                            name = "Nagpur Police Control Room (112)",
                            phoneNumber = "112",
                            relationship = "Police",
                            isWhatsAppEnabled = true,
                            isSmsEnabled = true,
                            isPrimary = true,
                            customNotes = "Nagpur City 24x7 Emergency Command"
                        ),
                        EmergencyContact(
                            name = "Nagpur Police WhatsApp Helpline",
                            phoneNumber = "+917122561222",
                            relationship = "Police",
                            isWhatsAppEnabled = true,
                            isSmsEnabled = false,
                            isPrimary = false,
                            customNotes = "Direct WhatsApp Citizen Alert Cell"
                        ),
                        EmergencyContact(
                            name = "Women Helpline Nagpur",
                            phoneNumber = "1091",
                            relationship = "Police",
                            isWhatsAppEnabled = true,
                            isSmsEnabled = true,
                            isPrimary = false,
                            customNotes = "Damini Squad Emergency Cell"
                        ),
                        EmergencyContact(
                            name = "Parent 2 / Alternate Guardian",
                            phoneNumber = "+919822334455",
                            relationship = "Parent",
                            isWhatsAppEnabled = true,
                            isSmsEnabled = true,
                            isPrimary = false,
                            customNotes = "Secondary Guardian"
                        )
                    )
                )
            }

            val safeZoneDao = database.safeZoneDao()
            if (safeZoneDao.getCount() == 0) {
                safeZoneDao.insertAll(
                    listOf(
                        SafeZone(
                            name = "Nagpur Police Commissionerate",
                            category = "Police Station",
                            latitude = 21.1524,
                            longitude = 79.0806,
                            address = "Civil Lines, Near High Court, Nagpur",
                            phoneNumber = "+917122561100",
                            is24Hours = true
                        ),
                        SafeZone(
                            name = "Sitabuldi Police Station",
                            category = "Police Station",
                            latitude = 21.1458,
                            longitude = 79.0832,
                            address = "Munje Square, Sitabuldi, Nagpur",
                            phoneNumber = "+917122561234",
                            is24Hours = true
                        ),
                        SafeZone(
                            name = "Sadar Police Station",
                            category = "Police Station",
                            latitude = 21.1610,
                            longitude = 79.0815,
                            address = "Residency Road, Sadar, Nagpur",
                            phoneNumber = "+917122561245",
                            is24Hours = true
                        ),
                        SafeZone(
                            name = "Dhantoli Police Station",
                            category = "Police Station",
                            latitude = 21.1328,
                            longitude = 79.0841,
                            address = "Near Mehadia Square, Dhantoli, Nagpur",
                            phoneNumber = "+917122561267",
                            is24Hours = true
                        ),
                        SafeZone(
                            name = "Ambazari Police Station",
                            category = "Police Station",
                            latitude = 21.1311,
                            longitude = 79.0558,
                            address = "Ambazari Garden Road, Nagpur",
                            phoneNumber = "+917122561289",
                            is24Hours = true
                        ),
                        SafeZone(
                            name = "Ganeshpeth Police Station",
                            category = "Police Station",
                            latitude = 21.1482,
                            longitude = 79.0984,
                            address = "Near Central Bus Stand, Ganeshpeth, Nagpur",
                            phoneNumber = "+917122561299",
                            is24Hours = true
                        ),
                        SafeZone(
                            name = "GMCH (Govt Medical College & Hospital)",
                            category = "Hospital",
                            latitude = 21.1275,
                            longitude = 79.0946,
                            address = "Medical Square, Hanuman Nagar, Nagpur",
                            phoneNumber = "+917122744489",
                            is24Hours = true
                        ),
                        SafeZone(
                            name = "AIIMS Nagpur",
                            category = "Hospital",
                            latitude = 21.0368,
                            longitude = 79.0305,
                            address = "Plot No. 2, Sector 20, MIHAN, Nagpur",
                            phoneNumber = "+917122755555",
                            is24Hours = true
                        ),
                        SafeZone(
                            name = "Nagpur Railway Station RPF / GRP Post",
                            category = "Police Station",
                            latitude = 21.1527,
                            longitude = 79.0886,
                            address = "Platform 1, Nagpur Central Railway Station",
                            phoneNumber = "139",
                            is24Hours = true
                        ),
                        SafeZone(
                            name = "Sitabuldi Metro Interchange Safety Booth",
                            category = "Metro Station",
                            latitude = 21.1449,
                            longitude = 79.0830,
                            address = "Sitabuldi Metro Station concourse, Nagpur",
                            phoneNumber = "+917122888800",
                            is24Hours = true
                        )
                    )
                )
            }
        }
    }
}
