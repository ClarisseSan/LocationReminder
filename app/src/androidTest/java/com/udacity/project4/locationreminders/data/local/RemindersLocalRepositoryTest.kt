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
    //Add testing implementation to the RemindersLocalRepository.kt

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    //Class under test
    private lateinit var remindersLocalRepository: RemindersLocalRepository
    private lateinit var database: RemindersDatabase

    @Before
    fun setupDbAndRepository() {

        //in memory database
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()

        // Get a reference to the class under test
        remindersLocalRepository =
            RemindersLocalRepository(database.reminderDao(), Dispatchers.Main)
    }

    @After
    fun finish() {
        //always close database
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
        val result = remindersLocalRepository.getReminder(reminder1.id) as Result.Success

        //then
        assertThat(result, `is`(notNullValue()))
        assertThat(result.data.title, `is`(reminder1.title))
        assertThat(result.data.description, `is`(reminder1.description))
        assertThat(result.data.latitude, `is`(reminder1.latitude))
        assertThat(result.data.longitude, `is`(reminder1.longitude))
        assertThat(result.data.location, `is`(reminder1.location))
    }

    @Test
    fun deleteAllReminders_emptyList() = runBlocking {
        //given
        val reminder1 = ReminderDTO(
            "Grocery",
            "Buy apples and bananas",
            "Savemore Festival mall",
            14.417399772472931,
            121.03937432329282
        )

        //when
        remindersLocalRepository.saveReminder(reminder1)
        remindersLocalRepository.deleteAllReminders()
        val result = remindersLocalRepository.getReminders() as Result.Success

        //then
        assertThat(result.data, `is`(emptyList()))
    }

    @Test
    fun getReminders_listNotNull() = runBlocking {
        //given
        val reminder1 = ReminderDTO(
            "Grocery",
            "Buy apples and bananas",
            "Savemore Festival mall",
            14.417399772472931,
            121.03937432329282
        )

        val reminder2 = ReminderDTO(
            "Books",
            "Buy manga books",
            "Fully Book Alabang",
            14.42221728701816,
            121.03025441349394
        )

        //when
        remindersLocalRepository.saveReminder(reminder1)
        remindersLocalRepository.saveReminder(reminder2)
        val result = remindersLocalRepository.getReminders() as Result.Success

        //then
        assertThat(result.data, `is`(notNullValue()))

    }

    @Test
    fun getReminderById_dataFound() = runBlocking{
        //given
        val reminder1 = ReminderDTO(
            "Grocery",
            "Buy apples and bananas",
            "Savemore Festival mall",
            14.417399772472931,
            121.03937432329282
        )

        //when
        remindersLocalRepository.saveReminder(reminder1)
        val result = remindersLocalRepository.getReminder(reminder1.id) as Result.Success

        //then
        assertThat(result.data, `is`(notNullValue()))

    }


    @Test
    fun getReminderById_dataNotFound() = runBlocking{
        //given
        val reminder1 = ReminderDTO(
            "Grocery",
            "Buy apples and bananas",
            "Savemore Festival mall",
            14.417399772472931,
            121.03937432329282
        )

        //when
        remindersLocalRepository.deleteAllReminders()
        val result = remindersLocalRepository.getReminder(reminder1.id) as Result.Error

        //then
        assertThat(result.message, `is`("Reminder not found!"))

    }


}