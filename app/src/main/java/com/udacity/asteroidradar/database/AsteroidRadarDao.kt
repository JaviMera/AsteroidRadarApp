package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface AsteroidRadarDao {

    @Insert(onConflict = REPLACE)
    suspend fun insert(asteroidEntity: List<AsteroidEntity>)

    @Query("select * from asteroid_table where close_approach_date >= :today order by close_approach_date")
    fun getAllAsteroids(today: Long) : LiveData<List<AsteroidEntity>>

}

