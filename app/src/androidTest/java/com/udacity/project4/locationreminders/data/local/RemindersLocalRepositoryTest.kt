package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.udacity.project4.locationreminders.data.dto.Result
import org.hamcrest.Matchers.notNullValue

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {
    //TODO: Add testing implementation to the RemindersLocalRepository.kt


    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    //Class under test
    private lateinit var remindersLocalRepository: RemindersLocalRepository
    private lateinit var database: RemindersDatabase

    @Before
    fun setup(){

        //in memory database
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java).allowMainThreadQueries().build()


        remindersLocalRepository = RemindersLocalRepository(database.reminderDao(), Dispatchers.Main)
    }

    @After
    fun finish(){
        database.close()
    }

    @Test
    fun saveReminder_getId() = runBlocking {
        //given
        val reminder1 = ReminderDTO(
            "Books",
            "Buy manga books",
            "Fully Book Alabang",
            14.42221728701816,
            121.03025441349394
        )

        //when
        remindersLocalRepository.saveReminder(reminder1)


        val result = remindersLocalRepository.getReminder(reminder1.id)
        result as Result.Success

        //then
        assertThat(result, `is`(notNullValue()))
        assertThat(result.data.title, `is`(reminder1.title))
        assertThat(result.data.description, `is`(reminder1.description))
        assertThat(result.data.latitude, `is`(reminder1.latitude))
        assertThat(result.data.longitude, `is`(reminder1.longitude))
        assertThat(result.data.location, `is`(reminder1.location))
    }


}