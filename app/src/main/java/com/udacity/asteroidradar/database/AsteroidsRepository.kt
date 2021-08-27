package com.udacity.asteroidradar.database

import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.api.AsteroidsDate

class AsteroidsRepository(private val database: AsteroidRadarDatabase, private val asteroidsDate: AsteroidsDate) {

    val asteroids = Transformations.map(database.asteroidRadarDao.getAllAsteroids(asteroidsDate.getCurrentDateLong())){
        it.toAsteroids()
    }

    suspend fun insertAsteroids(asteroidEntities: List<AsteroidEntity>){
        database.asteroidRadarDao.insert(asteroidEntities)
    }
}

