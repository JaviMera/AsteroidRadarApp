package com.udacity.asteroidradar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.*
import com.udacity.asteroidradar.work.SaveAsteroidsWorker
import timber.log.Timber
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }

        val constraints = Constraints.Builder()
            .setRequiresCharging(true)
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()

        val asteroidsWorkerRequest = PeriodicWorkRequestBuilder<SaveAsteroidsWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

        WorkManager
            .getInstance()
            .enqueueUniquePeriodicWork(
                SaveAsteroidsWorker.WORKER_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                asteroidsWorkerRequest
            )

        WorkManager.getInstance()
            .getWorkInfoByIdLiveData(asteroidsWorkerRequest.id)
            .observe(this, Observer {
                when (it.state) {
                    WorkInfo.State.SUCCEEDED -> {
                        Timber.i("Worker succeeded")
                    }
                    WorkInfo.State.FAILED -> {
                        Timber.i("Worker cancelled")
                    }
                    WorkInfo.State.RUNNING -> {
                        Timber.i("Worker running")
                    }
                }
            })
    }
}
