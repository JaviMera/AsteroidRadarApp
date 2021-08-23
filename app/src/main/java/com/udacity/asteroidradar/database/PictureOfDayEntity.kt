package com.udacity.asteroidradar.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "asteroid_picture_of_day_table")
data class PictureOfDayEntity(
    @PrimaryKey()
    var date: String = "",
    @ColumnInfo(name = "image_url")
    var imageUrl: String = "",
    @ColumnInfo(name = "title")
    var title: String = "",
    @ColumnInfo(name = "media_type")
    var mediaType: String = ""
)