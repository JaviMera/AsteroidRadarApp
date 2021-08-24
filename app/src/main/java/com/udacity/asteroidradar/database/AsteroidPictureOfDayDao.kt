package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AsteroidPictureOfDayDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pictureOfDayEntity: PictureOfDayEntity)

    @Query("select * from asteroid_picture_of_day_table limit 1")
    fun getPictureOfDay() : LiveData<PictureOfDayEntity>
}