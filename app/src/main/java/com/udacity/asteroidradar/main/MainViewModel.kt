package com.udacity.asteroidradar.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.api.NasaApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainViewModel : ViewModel() {

    companion object{
        const val API_KEY = "jXEDVjihcXsoNBIJKUF7w5Pm9MFri2faBV3m05a7"
    }

    private val _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroids: LiveData<List<Asteroid>>
    get() = _asteroids

    init {

        viewModelScope.launch {
            var getAsteroids = NasaApi.service.getAsteroids(
                API_KEY,
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                LocalDate.now().plusDays(7).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            )
            try{
                var result = getAsteroids.await()
                Log.i("MainViewModel", result)
                _asteroids.value = parseAsteroidsJsonResult(JSONObject(result))
            }catch(exception: Exception){
                Log.i("MainViewModel", "Error at retrieving asteroids:\n${exception.message.toString()}")
            }
        }
    }
}