package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.api.NasaApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidRadarDatabase
import com.udacity.asteroidradar.database.toAsteroidEntities
import com.udacity.asteroidradar.database.toAsteroids
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.await
import timber.log.Timber
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {

    companion object{
        const val API_KEY = "jXEDVjihcXsoNBIJKUF7w5Pm9MFri2faBV3m05a7"
    }

    private val database = AsteroidRadarDatabase.getInstance(application)

    private val _status = MutableLiveData<NasaApiStatus>()
    val status: LiveData<NasaApiStatus>
    get() = _status

    val dbAsteroids = Transformations.map(database.asteroidRadarDatabaseDao.getAllAsteroids()){

        it.toAsteroids()
    }

    fun updateStatus(nasaApiStatus: NasaApiStatus){
        _status.postValue(nasaApiStatus)
    }

    suspend fun getAsteroids() {

        val calendar = Calendar.getInstance()
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")

        val calendarFuture = Calendar.getInstance()
        calendarFuture.add(Calendar.DATE, 7)

        var asteroids = NasaApi.service.getAsteroids(
            API_KEY,
            simpleDateFormat.format(calendar.time),
            simpleDateFormat.format(calendarFuture.time)
        )

        try{
            _status.postValue(NasaApiStatus.LOADING)
            val result = asteroids.await()
            Timber.i(result)
            val asteroidList = parseAsteroidsJsonResult(JSONObject(result))
            database.asteroidRadarDatabaseDao.insert(asteroidList.toAsteroidEntities())
            _status.postValue(NasaApiStatus.DONE)
        }catch(exception: Exception){
            Timber.i("Error at retrieving asteroids:\n${exception.message.toString()}")
            _status.postValue(NasaApiStatus.ERROR)
        }
    }
}