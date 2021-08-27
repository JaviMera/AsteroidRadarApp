package com.udacity.asteroidradar.work

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.AsteroidsDate
import com.udacity.asteroidradar.api.NasaApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidRadarDatabase
import com.udacity.asteroidradar.database.AsteroidsRepository
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

    private val asteroidsRepository: AsteroidsRepository by lazy {
        AsteroidsRepository(
            AsteroidRadarDatabase.getInstance(applicationContext),
            AsteroidsDate(Constants.API_QUERY_DATE_FORMAT)
        ) }

    override suspend fun doWork(): Result {
        try{
            val asteroidsDate = AsteroidsDate(Constants.API_QUERY_DATE_FORMAT)

            val asteroidsRequest = NasaApi.asteroidsService.getAsteroids(
                MainViewModel.API_KEY,
                asteroidsDate.getCurrentDateString(),
                asteroidsDate.getFutureDateString(7)
            )

            val result = asteroidsRequest.await()
            val asteroids = parseAsteroidsJsonResult(JSONObject(result))
            asteroidsRepository.insertAsteroids(asteroids.toAsteroidEntities())

            return Result.success()
        }catch (exception: Exception){

            Timber.i(exception.message)
            return Result.failure()
        }
    }
}