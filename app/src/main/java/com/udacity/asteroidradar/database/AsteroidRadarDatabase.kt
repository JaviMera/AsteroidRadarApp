package com.udacity.asteroidradar.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [AsteroidEntity::class], version = 2, exportSchema = false)
abstract class AsteroidRadarDatabase : RoomDatabase() {

    abstract val asteroidRadarDatabaseDao: AsteroidRadarDatabaseDo

    companion object{
        @Volatile
        private var INSTANCE: AsteroidRadarDatabase? = null

        fun getInstance(context: Context) : AsteroidRadarDatabase {
            synchronized(this){
                var instance = INSTANCE

                if(instance == null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AsteroidRadarDatabase::class.java,
                        "asteroid_radar_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                }

                return instance
            }
        }
    }
}