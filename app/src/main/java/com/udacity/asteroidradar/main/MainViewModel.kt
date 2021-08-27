package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.AsteroidsDate
import com.udacity.asteroidradar.api.NasaApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.*
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

    private val asteroidsDate:AsteroidsDate by lazy { AsteroidsDate(Constants.API_QUERY_DATE_FORMAT) }

    private val asteroidsRepository = AsteroidsRepository(database, asteroidsDate)

    private val _status = MutableLiveData<NasaApiStatus>()
    val status: LiveData<NasaApiStatus>
    get() = _status

    private val _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroids: LiveData<List<Asteroid>>
    get() = _asteroids

    val dbAsteroids = asteroidsRepository.asteroids

    private val _picture = MutableLiveData<PictureOfDay>()
    val picture: LiveData<PictureOfDay>
    get() = _picture

    init {
        getWeekAsteroids()
        getPictureOfDay()
    }

    suspend fun getAsteroids() : String {

        var asteroids = NasaApi.asteroidsService.getAsteroids(
            API_KEY,
            asteroidsDate.getCurrentDateString(),
            asteroidsDate.getFutureDateString(7)
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
                val pictureOfDay = database.asteroidPictureOfDayDao.getPictureOfDay(asteroidsDate.getCurrentDateString())
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