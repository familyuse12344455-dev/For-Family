package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.models.BookingEntity
import com.example.data.models.CustomerProfileEntity
import com.example.data.models.NotificationEntity
import com.example.data.models.SavedAddressEntity
import com.example.data.models.VehicleEntity

@Database(
    entities = [
        BookingEntity::class,
        VehicleEntity::class,
        CustomerProfileEntity::class,
        SavedAddressEntity::class,
        NotificationEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class TorqfixDatabase : RoomDatabase() {
    abstract fun torqfixDao(): TorqfixDao

    companion object {
        @Volatile
        private var INSTANCE: TorqfixDatabase? = null

        fun getInstance(context: Context): TorqfixDatabase {
            com.example.data.remote.supabase.SupabaseSessionManager.init(context.applicationContext)
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TorqfixDatabase::class.java,
                    "torqfix_customer.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
