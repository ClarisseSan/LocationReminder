package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.get
import org.mockito.Mockito.mock
import org.koin.test.AutoCloseKoinTest
import org.mockito.Mockito.verify


@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest : AutoCloseKoinTest() {

    @get:Rule
    val instantExecutor = InstantTaskExecutorRule()


    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application

    //    TODO: test the navigation of the fragments.

    //    TODO: add testing for the error messages.

    //Use Koin to test code
    @Before
    fun init() {
        stopKoin()
        appContext = getApplicationContext()

        //A Koin module gather definitions that you will inject/combine for your application
        val myModule = module {
            //your dependencies here
            viewModel { RemindersListViewModel(appContext, get() as ReminderDataSource) }

            //declare a singleton bean definition
            single { SaveReminderViewModel(appContext, get() as ReminderDataSource) }
            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single { LocalDB.createRemindersDao(appContext) }


        }

        //Just start Koin to create instance definitions
        startKoin {
            modules(listOf(myModule))
        }

        //Retrieve ReminderDataSource instance
        repository = get()

        //delete all data to reset
        runBlocking {
            repository.deleteAllReminders()
        }
    }

    @After
    fun finish() {
        stopKoin()
    }


    //    TODO: test the displayed data on the UI.
    @Test
    fun reminders_DisplayedInUI(): Unit = runBlocking {

        //Given - add reminder to DB
        val reminder = ReminderDTO(
            "Grocery",
            "Buy apples and bananas",
            "Savemore Festival mall",
            14.417399772472931,
            121.03937432329282
        )

        val reminder2 = ReminderDTO(
            "Appliance",
            "Buy TV",
            "Ansons Landmark",
            14.417399772472931,
            121.03937432329282
        )
        repository.saveReminder(reminder)
        repository.saveReminder(reminder2)


        //When - RemindersListFragment launched to display reminders
        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        Thread.sleep(2000)

        //Then - Reminders are listed on the UI
        onView(withId(R.id.reminderssRecyclerView))
            .perform(
                RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                    hasDescendant(withText(reminder.title))
                )
            )
    }


}