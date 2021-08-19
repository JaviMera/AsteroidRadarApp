package com.udacity.asteroidradar.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.api.NasaApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.await
import timber.log.Timber
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainViewModel : ViewModel() {

    companion object{
        const val API_KEY = "jXEDVjihcXsoNBIJKUF7w5Pm9MFri2faBV3m05a7"
    }

    private val _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroids: LiveData<List<Asteroid>>
    get() = _asteroids

    private val _status = MutableLiveData<NasaApiStatus>()
    val status: LiveData<NasaApiStatus>
    get() = _status
    
    init {
        viewModelScope.launch {
           getAsteroids()
        }
    }

    suspend fun getAsteroids() {
        var asteroids = NasaApi.service.getAsteroids(
            API_KEY,
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            LocalDate.now().plusDays(7).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        )

        try{
            _status.postValue(NasaApiStatus.LOADING)
            val result = asteroids.await()
            Timber.i(result)
            _asteroids.postValue(parseAsteroidsJsonResult(JSONObject(result)))
            _status.postValue(NasaApiStatus.DONE)
        }catch(exception: Exception){
            Timber.i("Error at retrieving asteroids:\n${exception.message.toString()}")
            _status.postValue(NasaApiStatus.ERROR)
        }
    }
}