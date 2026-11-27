package com.chipandchill.smartmenu.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.chipandchill.smartmenu.data.entities.OrderDetail
import com.chipandchill.smartmenu.data.entities.OrderEntity
import com.chipandchill.smartmenu.data.entities.Product

@Database(
    entities = [
        Product::class,
        OrderEntity::class,
        OrderDetail::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SmartMenuDatabase : RoomDatabase() {

    abstract fun menuDao(): MenuDao

    companion object {

        @Volatile
        private var INSTANCE: SmartMenuDatabase? = null

        fun getDatabase(context: Context): SmartMenuDatabase {
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SmartMenuDatabase::class.java,
                    "smartmenu_database"
                ).build()

                INSTANCE = instance
                instance
            }
        }
    }
}
