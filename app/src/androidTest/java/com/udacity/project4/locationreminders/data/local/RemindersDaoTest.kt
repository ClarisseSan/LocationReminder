package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

//    TODO: Add testing implementation to the RemindersDao.kt

    //executes each task asynchronously using Acrchitecture components
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RemindersDatabase

    @Before
    fun initDb() {
        //setup database
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertReminder_GetById() = runBlockingTest {

        //Given - insert a reminder
        val reminder = ReminderDTO(
            "Grocery",
            "Buy apples and bananas",
            "Savemore Festival mall",
            14.417399772472931,
            121.03937432329282
        )
        database.reminderDao().saveReminder(reminder)

        //When - Get reminder by id from the database
        val loaded = database.reminderDao().getReminderById(reminder.id)

        //Then - loaded data contains the expected values
        assertThat(loaded as ReminderDTO, notNullValue())
        assertThat(loaded.id, `is`(reminder.id))
        assertThat(loaded.description, `is`(reminder.description))
        assertThat(loaded.location, `is`(reminder.location))
    }

    @Test
    fun deleteAllReminders_NullList() = runBlockingTest {
        //Given - insert a reminder
        val reminder = ReminderDTO(
            "Grocery",
            "Buy apples and bananas",
            "Savemore Festival mall",
            14.417399772472931,
            121.03937432329282
        )
        database.reminderDao().saveReminder(reminder)

        //When - Get reminder by id from the database
        database.reminderDao().deleteAllReminders()
        val result = database.reminderDao().getReminders()


        //Then
        assertThat(result, `is`(emptyList()))
    }

}