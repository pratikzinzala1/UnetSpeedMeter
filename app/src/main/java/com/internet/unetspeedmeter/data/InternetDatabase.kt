package com.internet.unetspeedmeter.data


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [InternetDataItem::class], version = 1, exportSchema = false)
abstract class InternetDatabase : RoomDatabase() {

    abstract fun internetItemDao(): InternetItemDao

    companion object {

        @Volatile
        private var Instance: InternetDatabase? = null

        fun getDatabase(context: Context): InternetDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, InternetDatabase::class.java, "internet_database")
                    .build().also {
                        Instance = it
                    }
            }


        }


    }


}