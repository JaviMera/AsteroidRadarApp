package com.udacity.asteroidradar.database

import android.graphics.Picture
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import java.text.SimpleDateFormat

fun List<Asteroid>.toAsteroidEntities() : List<AsteroidEntity> {
    return this.map {
        AsteroidEntity(
            asteroidId = it.id,
            codeName = it.codename,
            absoluteMagnitude = it.absoluteMagnitude,
            closeApproachDate = SimpleDateFormat("yyyy-MM-dd").parse(it.closeApproachDate)?.time!!,
            distanceFromEarth = it.distanceFromEarth,
            estimatedDiameter = it.estimatedDiameter,
            isPotentiallyHazardous = it.isPotentiallyHazardous,
            relativeVelocity = it.relativeVelocity
        )
    }
}

fun List<AsteroidEntity>.toAsteroids() : List<Asteroid>{
    return this.map {
        Asteroid(
            id = it.asteroidId,
            codename = it.codeName,
            absoluteMagnitude = it.absoluteMagnitude,
            closeApproachDate = SimpleDateFormat("yyyy-MM-dd").format(it.closeApproachDate),
            distanceFromEarth = it.distanceFromEarth,
            estimatedDiameter = it.estimatedDiameter,
            isPotentiallyHazardous = it.isPotentiallyHazardous,
            relativeVelocity = it.relativeVelocity
        )
    }
}

fun PictureOfDay.toPictureOfDayEntity() : PictureOfDayEntity{
    return PictureOfDayEntity(
        date = this.date,
        title = this.title,
        mediaType = this.mediaType,
        imageUrl = this.url
    )
}

fun PictureOfDayEntity.toPictureOfDay() : PictureOfDay?{

    return PictureOfDay(
        date = this.date,
        url = this.imageUrl,
        title = this.title,
        mediaType = this.mediaType
    )
}