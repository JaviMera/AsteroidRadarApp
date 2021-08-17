package com.udacity.asteroidradar.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.api.NasaApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainViewModel : ViewModel() {

    companion object{
        const val API_KEY = "jXEDVjihcXsoNBIJKUF7w5Pm9MFri2faBV3m05a7"
    }

    init {

        viewModelScope.launch {
            var getAsteroids = NasaApi.service.getAsteroids(
                API_KEY,
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                LocalDate.now().plusDays(7).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            )

            getAsteroids.enqueue(object: Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {

                    val asteroids = parseAsteroidsJsonResult(JSONObject(response.body().toString()))
                    val asteroid = asteroids[0]
                    Log.i("MainViewModel", "id: ${asteroid.id}, name: ${asteroid.codename}")
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.i("MainViewModel", t.message.toString())
                }
            })
        }
    }
}