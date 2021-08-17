package com.udacity.asteroidradar.api

import com.udacity.asteroidradar.Constants
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface NasaApiService {
    @GET("neo/rest/v1/feed")
    fun getAsteroids(
    @Query("api_key") apiKey: String,
    @Query("start_date") startDate: String,
    @Query("end_date") endDate: String
    ) : Call<String>
}

object NasaApi{
    val service: NasaApiService by lazy{
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(NasaApiService::class.java)
    }
}