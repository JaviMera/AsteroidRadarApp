package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AsteroidRadarDatabaseDo {

    @Insert
    suspend fun insert(asteroidEntity: AsteroidEntity)

    @Query("select * from asteroid_table")
    fun getAllAsteroids() : LiveData<List<AsteroidEntity>>
}