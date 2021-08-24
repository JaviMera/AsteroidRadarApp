package com.udacity.asteroidradar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.udacity.asteroidradar.work.SaveAsteroidsWorker
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }

        val asteroidsWorkerRequest = OneTimeWorkRequestBuilder<SaveAsteroidsWorker>()
            .build()

        WorkManager
            .getInstance()
            .enqueue(asteroidsWorkerRequest)
    }
}
