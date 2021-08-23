package com.udacity.asteroidradar.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
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

    @GET("planetary/apod")
    fun getPictureOfTheDay(
        @Query("api_key") apiKey: String
    ) : Call<PictureOfDay>
}

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

object NasaApi{
    val asteroidsService: NasaApiService by lazy{
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(NasaApiService::class.java)
    }

    val pictureOfDayService: NasaApiService by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(NasaApiService::class.java)
    }
}