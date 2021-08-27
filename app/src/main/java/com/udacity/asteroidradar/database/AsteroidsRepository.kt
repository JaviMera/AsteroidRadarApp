package com.udacity.asteroidradar.database

import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.AsteroidsDate

class AsteroidsRepository(private val database: AsteroidRadarDatabase, private val asteroidsDate: AsteroidsDate) {

    val asteroids = Transformations.map(database.asteroidRadarDao.getAllAsteroids(asteroidsDate.getCurrentDateLong())){
        it.toAsteroids()
    }

    suspend fun insertAsteroids(asteroidEntities: List<AsteroidEntity>){
        database.asteroidRadarDao.insert(asteroidEntities)
    }

    suspend fun getPictureOfDay(today: String) : PictureOfDayEntity? {
        return database.asteroidPictureOfDayDao.getPictureOfDay(today)
    }

    suspend fun insertPictureOfDay(pictureOfDayEntity: PictureOfDayEntity) {
        database.asteroidPictureOfDayDao.insert(pictureOfDayEntity)
    }

    suspend fun getMostRecentPictureOfDay() : PictureOfDayEntity?{
        return database.asteroidPictureOfDayDao.getMostRecentPictureOfDay()
    }
}

