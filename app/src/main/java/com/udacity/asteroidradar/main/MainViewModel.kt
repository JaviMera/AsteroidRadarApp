package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
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

    val dbAsteroids = Transformations.map(database.asteroidRadarDao.getAllAsteroids(getCurrentDateLong())){
        it.toAsteroids()
    }

    private val _picture = MutableLiveData<PictureOfDay>()
    val picture: LiveData<PictureOfDay>
    get() = _picture

    init {
        getWeekAsteroids()
        getPictureOfDay()
    }

    private fun getCurrentDateString() : String {
        val calendar = Calendar.getInstance()
        return SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT).format(calendar.time)
    }

    private fun getCurrentDateLong() : Long {
        val currentDate = getCurrentDateString()
        return SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT).parse(currentDate).time
    }

    private fun getFutureDateString(days: Int) : String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, days)
        return SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT).format(calendar.time)
    }

    suspend fun getAsteroids() : String {

        var asteroids = NasaApi.asteroidsService.getAsteroids(
            API_KEY,
            getCurrentDateString(),
            getFutureDateString(7)
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
                val formatter = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT)
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
                val pictureOfDay = database.asteroidPictureOfDayDao.getPictureOfDay(getCurrentDateString())
                if(pictureOfDay != null){
                    Timber.i("Getting image from database.\n${pictureOfDay}")
                    _picture.postValue(pictureOfDay.toPictureOfDay())
                }else{
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
                            val mostRecentPictureFromDb = database.asteroidPictureOfDayDao.getMostRecentPictureOfDay()?.toPictureOfDay()
                            mostRecentPictureFromDb?.let {
                                _picture.postValue(mostRecentPictureFromDb)
                            }
                        }
                    }
                }
            }catch(exception: Exception){
                Timber.i("Unable to download picture of day from nasa.\n${exception.message}")
            }
        }
    }
}