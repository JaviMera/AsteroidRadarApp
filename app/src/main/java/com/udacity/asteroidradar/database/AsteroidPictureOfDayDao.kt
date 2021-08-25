package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.udacity.asteroidradar.PictureOfDay

@Dao
interface AsteroidPictureOfDayDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pictureOfDayEntity: PictureOfDayEntity)

    @Query("select * from asteroid_picture_of_day_table where date = :today limit 1")
    fun getPictureOfDay(today: String) : LiveData<PictureOfDayEntity>

    @Query("select * from asteroid_picture_of_day_table")
    suspend fun getAllPicturesOfDay() : List<PictureOfDayEntity>

    @Delete(entity = PictureOfDayEntity::class)
    suspend fun deletePictures(pictures: List<PictureOfDayEntity>)

    @Query("select * from asteroid_picture_of_day_table order by date desc limit 1")
    suspend fun getMostRecentPictureOfDay(): PictureOfDayEntity?
}