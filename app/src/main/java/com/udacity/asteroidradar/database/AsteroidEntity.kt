package com.udacity.asteroidradar.database

import android.annotation.SuppressLint
import androidx.core.util.TimeUtils
import androidx.room.*
import com.udacity.asteroidradar.Asteroid
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

@Entity(tableName = "asteroid_table")
data class AsteroidEntity (
    @PrimaryKey()
    var asteroidId: Long = 0L,
    @ColumnInfo(name = "code_name")
    var codeName: String = "",
    @ColumnInfo(name = "close_approach_date")
    var closeApproachDate: Long = 0L,
    @ColumnInfo(name = "absolute_magnitude")
    var absoluteMagnitude: Double = 0.0,
    @ColumnInfo(name = "estimated_diameter")
    var estimatedDiameter: Double = 0.0,
    @ColumnInfo(name = "relative_velocity")
    var relativeVelocity: Double = 0.0,
    @ColumnInfo(name = "distance_from_earth")
    var distanceFromEarth: Double = 0.0,
    @ColumnInfo(name = "is_potentially_hazardous")
    var isPotentiallyHazardous: Boolean = false
)

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