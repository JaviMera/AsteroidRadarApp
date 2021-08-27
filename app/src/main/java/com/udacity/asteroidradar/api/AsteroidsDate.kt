package com.udacity.asteroidradar.api

import java.text.SimpleDateFormat
import java.util.*

class AsteroidsDate(private val dateFormat: String){

    fun getCurrentDateString() : String {
        val calendar = Calendar.getInstance()
        return SimpleDateFormat(dateFormat).format(calendar.time)
    }

    fun getCurrentDateLong() : Long {
        val currentDate = getCurrentDateString()
        return SimpleDateFormat(dateFormat).parse(currentDate).time
    }

    fun getFutureDateString(days: Int) : String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, days)
        return SimpleDateFormat(dateFormat).format(calendar.time)
    }
}