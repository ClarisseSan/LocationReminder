package com.udacity.project4.locationreminders.savereminder

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import com.udacity.project4.R
import org.junit.After
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    //provide testing to the SaveReminderView and its live data objects
    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    // Subject under test
    private lateinit var viewModel: SaveReminderViewModel

    private lateinit var remindersRepository: FakeDataSource


    @Before
    fun setupViewModel() = runBlocking {
        remindersRepository = FakeDataSource()
        viewModel =
            SaveReminderViewModel(ApplicationProvider.getApplicationContext(), remindersRepository)
    }

    @After
    fun finish() {
        //Stop Koin Application that was started
        stopKoin()
    }


    @Test
    fun validateEnteredData_missingData_invalidData() = runBlocking {
        val reminder1 = ReminderDataItem(
            "",
            "Buy apples and bananas",
            "Savemore Alabang",
            14.417399772472931,
            121.03937432329282
        )

        val reminder2 = ReminderDataItem(
            "Books",
            "Buy manga books",
            "",
            14.42221728701816,
            121.03025441349394
        )

        assertFalse(viewModel.validateEnteredData(reminder1))
        assertEquals(
            viewModel.showSnackBarInt.getOrAwaitValue(),
            R.string.err_enter_title
        )

        assertFalse(viewModel.validateEnteredData(reminder2))
        assertEquals(
            viewModel.showSnackBarInt.getOrAwaitValue(),
            R.string.err_select_location
        )
    }

    @Test
    fun validateEnteredData_completeData_validData() = runBlocking {
        val reminder = ReminderDataItem(
            "Grocery",
            "Buy apples and bananas",
            "Savemore Festival mall",
            14.417399772472931,
            121.03937432329282
        )

        assertTrue(viewModel.validateEnteredData(reminder))
    }

    @Test
    fun saveReminder_showToast() = runBlocking {
        //Given
        val reminder1 = ReminderDataItem(
            "Grocery",
            "Buy apples and bananas",
            "Savemore Festival mall",
            14.417399772472931,
            121.03937432329282
        )
        val reminder2 = ReminderDataItem(
            "Books",
            "Buy manga books",
            "Fully Book Alabang",
            14.42221728701816,
            121.03025441349394
        )

        //when
        viewModel.saveReminder(reminder1)
        viewModel.saveReminder(reminder2)

        //Then
        viewModel.showLoading.getOrAwaitValue()?.let { assertFalse(it) }

        assertEquals(
            viewModel.showToast.getOrAwaitValue(),
            ApplicationProvider.getApplicationContext<Context?>().applicationContext.resources.getString(
                R.string.reminder_saved
            )
        )

    }


}