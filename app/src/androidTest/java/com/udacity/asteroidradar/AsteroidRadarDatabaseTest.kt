package com.udacity.asteroidradar

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.udacity.asteroidradar.database.AsteroidEntity
import com.udacity.asteroidradar.database.AsteroidRadarDatabase
import com.udacity.asteroidradar.database.AsteroidRadarDatabaseDo
import org.hamcrest.CoreMatchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.lang.Exception

@RunWith(AndroidJUnit4::class)
class AsteroidRadarDatabaseTest {
    private lateinit var asteroidRadarDao: AsteroidRadarDatabaseDo
    private lateinit var database: AsteroidRadarDatabase

    @Before
    fun createDb(){
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(
            context,
            AsteroidRadarDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        asteroidRadarDao = database.asteroidRadarDatabaseDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb(){
        database.close()
    }

    @Test
    @Throws(Exception::class)
    suspend fun writeAsteroidAndRead(){
        val asteroid = AsteroidEntity()
        asteroidRadarDao.insert(listOf(asteroid))

        val asteroidFromDb = asteroidRadarDao.getAllAsteroids().value?.get(0)

        assertThat(asteroidFromDb, equalTo(asteroid))
    }
}