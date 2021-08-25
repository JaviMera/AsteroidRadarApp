package com.udacity.asteroidradar.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.api.NasaApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidRadarDatabase
import com.udacity.asteroidradar.database.toAsteroidEntities
import com.udacity.asteroidradar.main.MainViewModel
import org.json.JSONObject
import retrofit2.await
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class SaveAsteroidsWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams){

    companion object{
        const val WORKER_NAME = "SaveAsteroidsWorker"
    }

    override suspend fun doWork(): Result {
        try{
            val calendar = Calendar.getInstance()
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")

            val calendarFuture = Calendar.getInstance()
            calendarFuture.add(Calendar.DATE, 7)

            Timber.i(simpleDateFormat.format(calendar.time).toString())
            Timber.i(simpleDateFormat.format(calendarFuture.time).toString())

            val asteroidsRequest = NasaApi.asteroidsService.getAsteroids(
                MainViewModel.API_KEY,
                simpleDateFormat.format(calendar.time),
                simpleDateFormat.format(calendarFuture.time)
            )

            val result = asteroidsRequest.await()
            val asteroids = parseAsteroidsJsonResult(JSONObject(result))

            val asteroidsDao = AsteroidRadarDatabase.getInstance(applicationContext).asteroidRadarDao
            asteroidsDao.insert(asteroids.toAsteroidEntities())

            return Result.success()
        }catch (exception: Exception){

            return Result.failure()
        }
    }
}