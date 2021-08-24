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

    val dbAsteroids = Transformations.map(database.asteroidRadarDao.getAllAsteroids(System.currentTimeMillis())){
        it.toAsteroids()
    }

    val picture = Transformations.map(database.asteroidPictureOfDayDao.getPictureOfDay()){
        it.toPictureOfDay()
    }

    init {
        getWeekAsteroids()

//        viewModelScope.launch {
//            try{
//                _picture.value = database.asteroidPictureOfDayDao.getPictureOfDay().value
//                if(pictureFromDb.value != null){
//                    Timber.i("${pictureFromDb.value!!.title} ${pictureFromDb.value!!.imageUrl}")
//                }
//                else{
//                    Timber.i("Downloading picture")
//                    var pictureOfDayRequest = NasaApi.pictureOfDayService.getPictureOfTheDay(API_KEY)
//                    var result = pictureOfDayRequest.await()
//
//                    database.asteroidPictureOfDayDao.insert(result.toPictureOfDayEntity())
//                }
//
//            }catch (exception: Exception){
//                Timber.i("Unable to retrieve picture of day.\n${exception.message}")
//            }
//        }
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
                Timber.i(result)
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
//
//    fun getPictureOfDay() {
//        viewModelScope.launch {
//            var pictureOfDayRequest = NasaApi.pictureOfDayService.getPictureOfTheDay(API_KEY)
//            var result = pictureOfDayRequest.await()
//            database.asteroidPictureOfDayDao.insert(result.toPictureOfDayEntity())
//        }
//    }
}