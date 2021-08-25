package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.NasaApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidRadarDatabase
import com.udacity.asteroidradar.database.toAsteroids
import com.udacity.asteroidradar.database.toPictureOfDay
import com.udacity.asteroidradar.database.toPictureOfDayEntity
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.await
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {

    companion object{
        const val API_KEY = "jXEDVjihcXsoNBIJKUF7w5Pm9MFri2faBV3m05a7"
    }

    private val database = AsteroidRadarDatabase.getInstance(application)

    private val _status = MutableLiveData<NasaApiStatus>()
    val status: LiveData<NasaApiStatus>
    get() = _status

    private val _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroids: LiveData<List<Asteroid>>
    get() = _asteroids

    private val _currentDate = SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().time)

    val dbAsteroids = Transformations.map(database.asteroidRadarDao.getAllAsteroids(SimpleDateFormat("yyyy-MM-dd").parse(_currentDate).time)){
        it.toAsteroids()
    }

    private val _picture = MutableLiveData<PictureOfDay>()
    val picture: LiveData<PictureOfDay>
    get() = _picture

    init {
        getWeekAsteroids()
        getPictureOfDay()
    }

    suspend fun getAsteroids() : String {

        val calendar = Calendar.getInstance()
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")

        val calendarFuture = Calendar.getInstance()
        calendarFuture.add(Calendar.DATE, 7)

        var asteroids = NasaApi.asteroidsService.getAsteroids(
            API_KEY,
            simpleDateFormat.format(calendar.time),
            simpleDateFormat.format(calendarFuture.time)
        )

        return asteroids.await()
    }

    fun getWeekAsteroids(): Boolean {
        viewModelScope.launch {

            try{
                _status.postValue(NasaApiStatus.LOADING)
                val result = getAsteroids()
                _asteroids.postValue(parseAsteroidsJsonResult(JSONObject(result)))
                _status.postValue(NasaApiStatus.DONE)

            }catch(exception: Exception){
                Timber.i("Error at retrieving asteroids:\n${exception.message.toString()}")
                _status.postValue(NasaApiStatus.ERROR)
            }
        }

        return true
    }

    fun getTodaysAsteroids(): Boolean {
        viewModelScope.launch {

            try{
                _status.postValue(NasaApiStatus.LOADING)
                val result = getAsteroids()
                val asteroidList = parseAsteroidsJsonResult(JSONObject(result))
                val formatter = SimpleDateFormat("yyyy-MM-dd")
                val calendar = Calendar.getInstance()
                _asteroids.postValue(asteroidList.filter {
                    it.closeApproachDate == formatter.format(calendar.time)
                })
                _status.postValue(NasaApiStatus.DONE)

            }catch(exception: Exception){
                Timber.i("Error at retrieving asteroids:\n${exception.message.toString()}")
                _status.postValue(NasaApiStatus.ERROR)
            }
        }

        return true
    }

    private fun getPictureOfDay() {
        viewModelScope.launch {
            try {
                var pictureOfDayRequest = NasaApi.pictureOfDayService.getPictureOfTheDay(API_KEY)
                var result = pictureOfDayRequest.await()
                Timber.i(result.toString())

                when(result.mediaType){
                    "image" -> {
                        database.asteroidPictureOfDayDao.insert(result.toPictureOfDayEntity())
                        _picture.postValue(result)
                    }
                    "video" -> {
                        Timber.i("Picture of today is a video. We can't show a video :(")
                        val pictureOfDay = database.asteroidPictureOfDayDao.getMostRecentPictureOfDay()?.toPictureOfDay()
                        Timber.i("Picture of day from database: $pictureOfDay")
                        _picture.postValue(database.asteroidPictureOfDayDao.getMostRecentPictureOfDay()?.toPictureOfDay())
                    }
                }
            }catch(exception: Exception){
                Timber.i("Unable to download picture of day from nasa.\n${exception.message}")
            }
        }
    }
}